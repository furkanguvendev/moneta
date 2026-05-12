## **🪙 Moneta - Digital Wallet API**

Moneta, modern finansal işlemler için geliştirilmiş, güvenli ve yüksek tutarlılık odaklı bir Digital Wallet (Dijital Cüzdan) backend uygulamasıdır. Proje, ilişkisel veri modeli prensiplerine sadık kalınarak Supabase (PostgreSQL) altyapısı üzerine inşa edilmiştir.

### 🚀 Öne Çıkan Özellikler

* Kullanıcı Yönetimi: Supabase Auth (JWT) tabanlı güvenli kimlik doğrulama ve session yönetimi.

* Cüzdan İşlemleri: PostgreSQL'in "ACID" garantisiyle hatasız bakiye ve para birimi yönetimi.

* Transfer Sistemi: İlişkisel veri tabanı (RDBMS) gücüyle kullanıcılar arası güvenli para transferi.

* İşlem Geçmişi: Karmaşık sorgular ve join işlemleriyle optimize edilmiş detaylı finansal loglama.

### **🛠 Teknik Stack**

* Dil: Java 17+

* Framework: Spring Boot 3.x

* Veritabanı: Supabase (PostgreSQL) - Bulut tabanlı ilişkisel veritabanı.

* Güvenlik: JSON Web Token (JWT) ve Supabase Auth entegrasyonu.

* Dokümantasyon: Swagger / OpenAPI 3.0

### **🏗 Mimari Yapı**

* Proje, veritabanı tutarlılığını ön planda tutan katmanlı bir mimari ile geliştirilmiştir:

* Controller: RESTful API uç noktalarının yönetimi.

* Service: Finansal validasyonların ve iş mantığının yürütüldüğü katman.

* Repository: Spring Data JPA ile PostgreSQL erişimi.

* Entity/DTO: Veritabanı şemaları ve güvenli veri transfer nesneleri.