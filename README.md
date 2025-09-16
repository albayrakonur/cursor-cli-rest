# Agent CLI - Spring Boot REST API

Bu proje, cursor-agent komutlarını HTTP endpoint'i üzerinden çalıştırmak için geliştirilmiş bir Spring Boot uygulamasıdır.

## Özellikler

- 🚀 Asenkron komut çalıştırma
- ⏱️ Timeout kontrolü (30 saniye)
- 🔧 Thread pool tabanlı yürütme
- 📡 REST API ile komut gönderme
- 🌐 Cross-platform destek (Windows/Linux)

## Teknolojiler

- **Java 17+**
- **Spring Boot 3.x**
- **Maven**
- **ThreadPoolTaskExecutor**
- **CompletableFuture**

## Kurulum

### Gereksinimler

- Java 17 veya üzeri
- Maven 3.6+
- cursor-agent CLI aracı (sistem PATH'inde olmalı)

### Projeyi Çalıştırma

1. Projeyi klonlayın:
```bash
git clone <repository-url>
cd agent-cli
```

2. Bağımlılıkları yükleyin ve uygulamayı çalıştırın:
```bash
mvn spring-boot:run
```

3. Uygulama `http://localhost:8080` adresinde çalışmaya başlayacaktır.

## API Kullanımı

### Komut Çalıştırma

**Endpoint:** `POST /cursor-cli/ask`

**Content-Type:** `application/json`

**Request Body:**
```json
{
  "prompt": "Your cursor-agent prompt here"
}
```

**Response:**
```json
"exit=0\nCommand output here..."
```

### Örnek Kullanımlar

#### cURL ile:
```bash
curl -X POST http://localhost:8080/cursor-cli/ask \
  -H "Content-Type: application/json" \
  -d '{"prompt": "What is the current date?"}'
```

#### PowerShell ile:
```powershell
Invoke-RestMethod -Uri 'http://localhost:8080/cursor-cli/ask' `
  -Method POST `
  -ContentType 'application/json' `
  -Body '{"prompt": "List files in current directory"}'
```

#### JavaScript ile:
```javascript
fetch('http://localhost:8080/cursor-cli/ask', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    prompt: 'Your prompt here'
  })
})
.then(response => response.text())
.then(data => console.log(data));
```

## Konfigürasyon

### application.properties

```properties
spring.application.name=agent-cli
server.port=8080
```

### Thread Pool Ayarları

Thread pool konfigürasyonu `ExecutorConfig` sınıfında yapılabilir:

```java
@Bean(name = "commandExecutor")
public ThreadPoolTaskExecutor commandExecutor() {
    ThreadPoolTaskExecutor exe = new ThreadPoolTaskExecutor();
    exe.setCorePoolSize(50);     // Minimum thread sayısı
    exe.setMaxPoolSize(100);     // Maksimum thread sayısı
    exe.setQueueCapacity(200);   // Kuyruk kapasitesi
    exe.setThreadNamePrefix("cursor-cli-exec-");
    exe.initialize();
    return exe;
}
```

## Güvenlik Notları

⚠️ **DİKKAT:** Bu uygulama herhangi bir güvenlik filtresi içermez ve gelen tüm komutları çalıştırır. Üretim ortamında kullanmadan önce:

- Komut whitelist'i ekleyin
- Authentication/Authorization ekleyin
- Input validation yapın
- Rate limiting uygulayın

## Test Etme

### Unit Testler
```bash
mvn test
```

### Manuel Test
```bash
# Uygulamayı çalıştırın
mvn spring-boot:run

# Başka bir terminal'de test edin
curl -X POST http://localhost:8080/cursor-cli/ask \
  -H "Content-Type: application/json" \
  -d '{"prompt": "help"}'
```

## Troubleshooting

### Sık Karşılaşılan Sorunlar

1. **cursor-agent komutu bulunamıyor**
   - cursor-agent'ın sistem PATH'inde olduğundan emin olun
   - `cursor-agent --version` komutunu test edin

2. **Timeout hataları**
   - Komut süresini `runCommandWithTimeout` metodunda artırın
   - Büyük çıktı üreten komutlar için buffer boyutunu kontrol edin

3. **Port 8080 kullanımda**
   - `application.properties`'de port değiştirin: `server.port=8081`

## Geliştirme

### Proje Yapısı

```
src/
├── main/
│   ├── java/
│   │   └── com/albayrak/agent_cli/
│   │       ├── AgentCliApplication.java
│   │       ├── controller/
│   │       │   └── CursorCliController.java
│   │       └── configuration/
│   │           └── ExecutorConfig.java
│   └── resources/
│       └── application.properties
└── test/
    └── java/
        └── com/albayrak/agent_cli/
            └── AgentCliApplicationTests.java
```

### Katkıda Bulunma

1. Fork edin
2. Feature branch oluşturun (`git checkout -b feature/amazing-feature`)
3. Commit edin (`git commit -m 'Add some amazing feature'`)
4. Push edin (`git push origin feature/amazing-feature`)
5. Pull Request açın

## Lisans

Bu proje MIT lisansı altında dağıtılmaktadır.
