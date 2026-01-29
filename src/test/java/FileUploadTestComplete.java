import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.FilePayload;
import com.microsoft.playwright.options.FormData;
import com.microsoft.playwright.options.RequestOptions;

public class FileUploadTestComplete {
    
    @Test
    void testFileUploadAndDownload() {
        Playwright playwright = null;
        APIRequestContext request = null;
        
        try {
            // === 1. ИНИЦИАЛИЗАЦИЯ ===
            playwright = Playwright.create();
            request = playwright.request().newContext();
            
            // === 2. ГЕНЕРАЦИЯ PNG ФАЙЛА В ПАМЯТИ ===
            // Создаем минимальный валидный PNG файл, 1x1 пиксель
            byte[] pngData = new byte[] {
                (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, // PNG сигнатура
                0x00, 0x00, 0x00, 0x0D,                               // Длина IHDR
                0x49, 0x48, 0x44, 0x52,                               // "IHDR"
                0x00, 0x00, 0x00, 0x01,                               // Ширина: 1
                0x00, 0x00, 0x00, 0x01,                               // Высота: 1
                0x08, 0x02, 0x00, 0x00, 0x00,                         // 8-bit RGB
                0x00, 0x00, 0x00, 0x00,                               // CRC
                0x00, 0x00, 0x00, 0x00,                               // Длина IDAT
                0x49, 0x44, 0x41, 0x54,                               // "IDAT"
                0x78, 0x63, 0x00, 0x00, 0x00, 0x01, 0x00, 0x01,       // Данные
                0x00, 0x00, 0x00, 0x00,                               // CRC
                0x00, 0x00, 0x00, 0x00,                               // Длина IEND
                0x49, 0x45, 0x4E, 0x44,                               // "IEND"
                (byte) 0xAE, 0x42, 0x60, (byte) 0x82                  // CRC
            };
            
            System.out.println("Сгенерирован PNG файл (" + pngData.length + " байт)");
            
            // Проверяем PNG сигнатуру исходного файла
            assertEquals(137, pngData[0] & 0xFF, "Первый байт PNG сигнатуры (0x89)");
            assertEquals(80, pngData[1] & 0xFF, "Второй байт PNG сигнатуры (0x50, 'P')");
            assertEquals(78, pngData[2] & 0xFF, "Третий байт PNG сигнатуры (0x4E, 'N')");
            assertEquals(71, pngData[3] & 0xFF, "Четвертый байт PNG сигнатуры (0x47, 'G')");
            
            // === 3. ЗАГРУЗКА ФАЙЛА ===
            FilePayload testFile = new FilePayload(
                "test-image.png", 
                "image/png", 
                pngData
            );

            APIResponse uploadResponse = request.post(
                "https://httpbin.org/post",
                RequestOptions.create().setMultipart(
                    FormData.create().set("file", testFile)
                )
            );
            
            // Проверяем успешность загрузки
            assertEquals(200, uploadResponse.status(), "Статус ответа должен быть 200");
            
            // === 4. ПРОВЕРКА ПОЛУЧЕНИЯ ФАЙЛА ===
            String responseBody = uploadResponse.text();
            assertTrue(responseBody.contains("data:image/png;base64"),
                "Ответ должен содержать base64 данные PNG");
            
            System.out.println("Сервер получил PNG файл");
            
            // === 5. ВЕРИФИКАЦИЯ СОДЕРЖИМОГО ===
            // Извлекаем base64 данные из ответа
            String base64Data = responseBody.split("\"file\": \"")[1].split("\"")[0];
            // Убираем префикс "data:image/png;base64,"
            base64Data = base64Data.split(",")[1];
            
            // Декодируем и проверяем
            byte[] receivedBytes = Base64.getDecoder().decode(base64Data);
            
            // Проверяем целостность данных
            assertArrayEquals(pngData, receivedBytes,
                "Содержимое загруженного файла должно совпадать с исходным");
            
            System.out.println("Содержимое файла сохранено без изменений");
            
            // Проверяем PNG сигнатуру полученного файла
            assertEquals(137, receivedBytes[0] & 0xFF, "PNG сигнатура полученного файла (0x89)");
            assertEquals(80, receivedBytes[1] & 0xFF, "PNG сигнатура полученного файла (0x50)");
            
            // === 6. СКАЧИВАНИЕ ЭТАЛОННОГО PNG ===
            System.out.println("Скачиваем эталонный PNG файл.");
            APIResponse downloadResponse = request.get("https://httpbin.org/image/png");
            
            assertEquals(200, downloadResponse.status(),
                "Эталонный PNG должен успешно скачаться");
            
            // === 7. ПРОВЕРКА MIME-ТИПА ===
            String contentType = downloadResponse.headers().get("content-type");
            assertNotNull(contentType, "Должен быть указан content-type");
            assertEquals("image/png", contentType, "MIME-тип должен быть image/png");
            
            System.out.println("MIME-тип корректный: " + contentType);
            
            // === 8. ВАЛИДАЦИЯ ФОРМАТА ЧЕРЕЗ СИГНАТУРУ ===
            byte[] downloadedContent = downloadResponse.body();
            assertTrue(downloadedContent.length > 0,
                "Скачанный файл не должен быть пустым");
            
            // Проверяем PNG сигнатуру (используем десятичные значения)
            assertEquals(137, downloadedContent[0] & 0xFF,
                "Первый байт PNG сигнатуры эталонного файла (0x89)");
            assertEquals(80, downloadedContent[1] & 0xFF,
                "Второй байт PNG сигнатуры эталонного файла (0x50, 'P')");
            assertEquals(78, downloadedContent[2] & 0xFF,
                "Третий байт PNG сигнатуры эталонного файла (0x4E, 'N')");
            assertEquals(71, downloadedContent[3] & 0xFF,
                "Четвертый байт PNG сигнатуры эталонного файла (0x47, 'G')");
            
            System.out.println("PNG сигнатура эталонного файла корректна");
            
            // === 9. ДОПОЛНИТЕЛЬНАЯ ПРОВЕРКА ===
            // Ищем "IHDR" в файле (должен быть в PNG)
            boolean hasIHDR = false;
            for (int i = 0; i < downloadedContent.length - 4; i++) {
                if (downloadedContent[i] == 0x49 &&  // I (73 в десятичной)
                    downloadedContent[i+1] == 0x48 &&  // H (72)
                    downloadedContent[i+2] == 0x44 &&  // D (68)
                    downloadedContent[i+3] == 0x52) {  // R (82)
                    hasIHDR = true;
                    break;
                }
            }
            assertTrue(hasIHDR, "PNG файл должен содержать IHDR chunk");
            
            System.out.println("Все проверки выполнены успешно.");
            
        } catch (Exception e) {
            System.err.println("Ошибка при выполнении теста: " + e.getMessage());
            e.printStackTrace();
            fail("Тест завершился с ошибкой: " + e.getMessage());
            
        } finally {
            // === 10. ОЧИСТКА РЕСУРСОВ ===
            System.out.println( "Очистка ресурсов...");
            if (request != null) {
                request.dispose();
            }
            if (playwright != null) {
                playwright.close();
            }
        }
    }
}