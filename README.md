#Warehouse Management System

Uygulama Özellikleri

Depo yönetim sistemi ile, bir şirketin ürünlerini sakladığı, listelediği, depolar arasında transfer yapabildiği, depolar ve ürünler üzerinde CRUD işlemlerini yapabildiği bir sistem yazıyor olacağız.

Gereksinimler
Spring Boot
Spring Web
Jpa
Postgres/H2/MSSQL/MySql
Lombok
Ön yüz olarak Thymeleaf kullanılmıştır.

Fonksiyonlar
Kullanıcı sisteme email-şifre kombinasyonu ile girmeli.
Sisteme girildikten sonra kayıtlı tüm depoların listelenmesi gerekmektedir. ( DELETED statüsündeki depolar listelenmeyecek )
Listelenen Depo tıklanıldığında, depo içerisindeki (DELETED statüsünde olmayan ) ürünler listelenecektir.
Listelenen Depo ekranında depolar üzerinde işlemler yapılmalıdır.
Listelenen ürüne tıklanıldığında açılacak bir pencerede ürüne ait bilgiler gösterilecektir.
Ürün listeleme ekranında ürünün depodaki miktarı değiştirilebilecektir.
Ürün listeleme ekranında o ürün başka bir depoya transfer edilebilecektir.
Bir ürüne tıklanıldığında o ürünün hangi depolarda bulunduğunu, toplam adet sayısı gibi özet bilgileri gösterilecektir.
Bir deponun özet bilgisi görülebilecektir. Özet bilgi içerisinde toplam ürün sayısı, toplam ürün fiyatı gibi bilgiler.
Bunun dışında eklemek istediğiniz özellikler varsa ekleyebilirsiniz.

Bu API ile sistemde kayıtlı Depo'lara yine sistemde kayıtlı bulunan ürünlerin stok miktarlarını tutacağız. Bir ürün aratıldığında o ürünün hangi depoda kaç adet ürün olduğunu görebildiğimiz gibi dilediğimizde ürün çıkartma / ekleme ve ürün stoğunu güncelleme gibi işlemleri yapacağız.

Modeller

Warehouse
Product
ProductWarehouse
User
Warehouse

Depomuzun özellikleri aşağıdaki gibidir.

Depo ID'si (ID) (Primary Key)
Depo Kodu (Code)
Depo'nun ismi (Name)
Deponun statüsü ( WarehouseStatus {ACTIVE, PASSIVE, DELETED} )
Kayıt Tarihi (CreateDate)
Güncelleme Tarihi (UpdateDate)
Product Ürüne ait özellikle aşağıodaki gibidir.

Ürün ID'si (ID) ( Primary Key )
Ürün Kodu (Code)
Ürünün ismi (Name)
Ürünün KDV'si (VATRate)
Ürünün KDV Fiyatı (VatAmount)
Ürünün KDV'siz Fiyatı (BasePrice)
Ürünün KDV Dahil fiyatı (VatIncludedPrice)
Ürünün durumu ( ProductStatus {ACTIVE, PASSIVE} )
Kayıt Tarihi (CreateDate)
Güncelleme Tarihi (UpdateDate)
User Kullanıcıya ait özellikler aşağıdaki gibidir.

ID ( Primary Key )
User Code (Code)
User Name (Name)
Email (Email)
Password (Password) hashli tutulmalı
Kullanıcının statüsü ( UserStatus {ACTIVE, PASSIVE, DELETED} )
Kayıt Tarihi (CreateDate)
Güncelleme Tarihi (UpdateDate)
ProductWarehouse Bu model İlişkisel olarak depo - ürün bilgisini tutacaktır. Özellikleri aşağıdaki gibidir.

Ürün ID'si (ProductId)
Depo ID'si (WarehouseId)
Stok Miktarı (StockAmount)
İşlem zamanı (TransactionDate)
İşlemi yapan kullanıcı ID'si (CreatedBy)
Fonksiyonlar
WarehouseController

Depo Listeleme

Method Name = list
HTTP Request Type = GET
End Point = /warehouseapi/warehouses
Kondisyon = Tüm aktif depolar listelenmeli. Dilerseniz aktif olup olmama durumunu requestten alabileceğiniz gibi sadece aktif olanları da listeleyebilirsiniz.
Depo Yaratma

Method Name = create
HTTP Request Type = POST
End Point = /warehouseapi/warehouses
Kondisyon = Depo ismi kesinlikle olmalı, depo ismi girilmemişse exception fırlatılıp uyarı verilmeli. Ayrıca, aynı depo kodundan en fazla bir adet olmalı, bir depo kodu sadece bir kere kullanılmalı. Depo yaratıldığında default olarak statüsü aktif olmalı.
Depo Güncelleme

Method Name = update
HTTP Request Type = PUT
End Point = /warehouseapi/warehouses/{warehouseId}
Kondisyon = DB'de kayıtlı bir depo yok ise hata fırlatılmalı. Güncelleme işleminde depo'nun statüsü DELETED olarak seçilmişse depo içerisinde stoğu olan ürün var mı kontrol edilmeli, eğer stok var ise depo silinemememli. Önce transfer yapılmalı uyarısı verilmeli.
Depo Silme

Method Name = delete
HTTP Request Type = DELETE
End Point = /warehouseapi/warehouses/{warehouseId}
Transfer

Method Name = transfer
HTTP Request Type = POST
EndPoint = /warehouseapi/warehouses/transfer/{fromWarehouseId}/{toWarehouseId}
Kondisyon = Her iki deponun durumu aktif olmalı, outgoing depodaki tüm ürünler incoming deposuna aktarılmalı. Bu kod bloğu transactional olmalı, herhangi bir yerde hata alırsa o ana kadar yapılan tüm işlemler geri alınabilir olmalı.
Yukarıdaki end-pointlerin detaylı açıklaması şöyledir.

Kullanıcı N adet depo oluşturabilir ve oluşturduğu bu depoların tamamını listeleyebilir. Bir depo silinmek istenildiğinde ilgili deponun içerisinde stoğu 0'dan büyük herhangi bir ürün olmamalıdır. Depolar kayıt edildiğinde kayıt tarihi atılmalı ve bir daha bu alan asla güncellenmemeli. Depo güncellenirse, güncellenme tarihi değiştirilmeli.

ProductController

Ürün Listeleme

Method Name = list
HTTP Request Type = GET
End Point = /warehouseapi/products
Kondisyon = Tüm aktif ürünler listelenmeli. Dilerseniz aktif olup olmama durumunu requestten alabileceğiniz gibi sadece aktif olanları da listeleyebilirsiniz.
Ürün Yaratma

Method Name = create
HTTP Request Type = POST
End Point = /warehouseapi/products
Kondisyon = Ürüne ait tüm özellikler mutlaka requestten gelmeli, herhangi bir alan dolu değilse hata fırlatılmalı. Aynı ürün kodu ile birden fazla ürün olmamalı, girilen ürün kodu sistemde mevcutsa hata fırlatılmalı. Ürünün fiyatı, KDV oranı, KDV'si, KDV'li miktarı gibi alanlar sıfırdan büyük olmalı.
Ürün Güncelleme

Method Name = update
HTTP Request Type = PUT
End Point = /warehouseapi/products/{productId}
Kondisyon = DB'de kayıtlı bir ürün yok ise hata fırlatılmalı. Aynı ürün kodu ile birden fazla ürün olmamalı, girilen ürün kodu sistemde mevcutsa hata fırlatılmalı. Ürünün fiyatı, KDV oranı, KDV'si, KDV'li miktarı gibi alanlar sıfırdan büyük olmalı.
Ürün Silme

Method Name = delete
HTTP Request Type = DELETE
End Point = /warehouseapi/products/{productId}
Kondisyon = Ürün silinmeden önce mutlaka stok bilgisine bakılmalı. Depolar içerisinde ilgili ürüne ait stoğu 0'dan büyük bir kayıt var ise ürün silinmemeli ve hata fırlatılmalı..
StockController

Transfer

Method Name = transfer
HTTP Request Type = GET
EndPoint = /warehouseapi/stocks/{productId}/transfer/{fromWarehouseId}/{toWarehouseId}
Kondisyon = Her iki deponun durumu aktif olmalı. Ürünün durumu aktif olmalı. İlgili ürünün kaydı aktarılan depodan silinip aktarılacak depoya eklenmeli. Eğer ilgili ürün aktarılacak depoda mevcutsa ürünün stok miktarı arttırılmalı. Bu kod bloğu transactional olmalı, herhangi bir yerde hata alırsa o ana kadar yapılan tüm işlemler geri alınabilir olmalı.
Güncelleme

Method Name = update
HTTP Request Type = POST
End Point = /warehouseapi/stocks
Kondisyon = Request Body olarak gönderilen obje içerisinde ürün ID'si, Depo ID'si ve yeni stock miktarı girilmeli. İlgili ürün ilgili depoda bulunamazsa hata fırlatılmalı. İlgili ürün bulanamazsa hata fırlatılmalı. Ürün ve Depo'nun durumları aktif olmalı eğer değilse hata fırlatılmalı. Ürüne ait yeni stock miktarı 0'dan küçük olmamalı.
Özet Bilgi

Method Name = summaries
HTTP Request Type = GET
End Point = /warehouseapi/stocks
Kondisyon = Bu method ile hangi depoda hangi üründen kaç adet var, bu ürünlerin KDV'li, KDV'siz toplam fiyatları ile toplam KDV bilgilerini de gönderiyor olacağız.
UserController

Login

Method Name = login
HTTP Request Type = POST
EndPoint = /warehouseapi/user/login
Kondisyon = Kullanıcının email ve şifresinin kontrolü yapılmalı. Email için validasyon uygulanmalı. Email veya şifre uyumlu değilse kullanıcıya bilgi verilmeli.
