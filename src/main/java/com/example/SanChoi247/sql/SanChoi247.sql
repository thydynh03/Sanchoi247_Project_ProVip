create database SanChoi247;
use SanChoi247;

create table users(
	uid int auto_increment primary key,
    name varchar(64),
    dob date,
    gender varchar(1),
    phone varchar(16),
    email varchar(50),
    username varchar(64),
    password varchar(128),
    avatar text,
    ten_san varchar(50),
    address varchar(256),
    img_san1 text,
    img_san2 text,
    img_san3 text,
    img_san4 text,
    img_san5 text,
    status int,
    role varchar(1)
);

create table loaiSan(
	loai_san_id int auto_increment primary key,
    loai_san_type varchar(50)
);

create table size(
	size_id int auto_increment primary key,
    size varchar(20)
);

create table san(
	san_id int auto_increment primary key,
    uid int,
    foreign key (uid) references users(uid),
    loai_san_id int,
    foreign key(loai_san_id) references loaiSan(loai_san_id),
    vi_tri_san varchar(20),
    size_id int,
    foreign key (size_id) references size(size_id),
	img text,
    is_approve int,
    eyeview int default 0
);

CREATE TABLE Schedulebooking (
    Sbooking_id INT AUTO_INCREMENT PRIMARY KEY,
    san_id INT,
    start_time TIME,
    end_time TIME,
    status VARCHAR(20), -- 'booked', 'available'
    price float, -- Thêm trường giá tiền
    FOREIGN KEY (san_id) REFERENCES san(san_id),
    booking_date DATE
);

create table booking(
	booking_id int auto_increment primary key,
    date timestamp,
    uid int,
    foreign key(uid) references users(uid),
    san_id int,
    foreign key (san_id) references san(san_id),
	slot int,
    foreign key (slot) references Schedulebooking(Sbooking_id),
    price double,
    status tinyint,
    vnpay_data json
);

create table rating(
	rating_id int auto_increment primary key,
    star int,
    uid int,
    foreign key (uid) references users(uid),
    booking_id int,
    foreign key (booking_id) references booking(booking_id)
);

create table refundBooking(
	booking_id int auto_increment primary key,
    foreign key (booking_id) references booking(booking_id),
    total int,
    created_on timestamp,
    status tinyint,
    approved_on timestamp,
    refunded_on timestamp,
    actual_refund int
);

create table userSecret(
	id int auto_increment primary key,
    uid int,
    foreign key (uid) references users(uid),
    secret_key varchar(256),
    create_at timestamp
);

create table googleOauth(
	client_id varchar(255),
    client_secret varchar(255),
    redirect_uri varchar(255)
);

insert into users(name, dob, gender, phone, email, username, password, avatar,ten_san, address, img_san1, img_san2, img_san3, img_san4, img_san5,status, role) values
('Nguyễn Đình Bảo Ân', '2004-03-19', 'M', '0987689426', 'anndb1903@gmail.com', 'a', 'a', 'https://image.spreadshirtmedia.com/image-server/v1/compositions/T210A2PA4301PT17X22Y0D1033855154W28793H34552/views/1,width=550,height=550,appearanceId=2,backgroundColor=000000,noPt=true/my-other-computer-is-your-computer-hacker-meme-cyb-mens-t-shirt.jpg',null , null, null, null, null,null,null,0, 'A'),
('Nguyễn Đình Thi', '2004-05-03', 'M', '000000000', 'tuanden090304@gmail.com', 'b', 'b', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSm64c617Ru1xbzmjJNIOzNYt5xMvNcB56l9Q&s','Cung thể thao Tuyên Sơn', 'Nại Nam 2, Hòa Cường Bắc, Hải Châu, Đà Nẵng','https://top10vietnam.top/wp-content/uploads/2021/08/Tong-hop-10-san-bong-da-cho-thue-uy-tin-tai-Da-Nang-1-600x400.jpg','https://dufago.com.vn/wp-content/uploads/2020/08/Cung-th%E1%BB%83-thao-Tuy%C3%AAn-S%C6%A1n-%C4%90%C3%A0-N%E1%BA%B5ng.jpg','https://yousport.vn/Media/Articles/080321051105526/nhung-mat-san-bong-da-pho-bien-nhat-hien-nay-banner.jpg','https://bulbal.vn/wp-content/uploads/2023/01/TOP-10-SAN-BONG-DA-PHUI-TAI-TPHCM-NAM-2023-2.jpg','https://sonsanepoxy.vn/wp-content/uploads/2023/08/kich-thuoc-san-bong-da-1.jpg',0, 'C'),
('Huỳnh Phạm Bảo Tuân', '2024-01-01', 'F', '1111111111', 'tuantuantuan@gmail.com', 'c', 'c', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcROjfDTghEmMns99jBtOkpQrfBN19Xfw4W2Kg&s', null,null, null,null,null,null,null,0, 'U' ),
('Châu Thành Trung', '2004-02-02', 'M', '2222222222', 'concatre@gmail.com', 'd', 'd', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRfA74jKoBCGxNadWXx6CI2uQiOrdOyh53LPQ&s','Sân bóng Chuyên Việt','98 Tiểu La, Hòa Thuận Đông, Hải Châu, Đà Nẵng', 'https://uyenuong.net/wp-content/uploads/2022/06/dien-tich-san-5-nguoi-3.jpg','https://www.tapdoanconhantao.com/wp-content/uploads/2019/07/thu-nhap-cao-nho-kinh-doanh-san-bong-da-co-nhan-tao.jpg','https://thegioiconhantao.com.vn/wp-content/uploads/2016/11/dien1.jpg','https://golfstore.vn/images/pro/1_3165.jpg','https://foba.vn/wp-content/uploads/2020/09/Hinh-anh-%E2%80%93-2020-Hoan-Thanh-Cong-Trinh-San-Bong-Binh-Thuan-1.jpg',0,'C'),
('Phạm Thanh Tùng', '2004-03-03', 'F', '3333333333', 'tung123@gmail.com', 'e', 'e', 'https://i.pinimg.com/550x/20/48/95/2048958ccc6ead899f8884a59d64ec72.jpg', null, null,null,null,null,null,null,0, 'U');

insert into users(name, dob, gender, phone, email, username, password, avatar,ten_san, address, img_san1, img_san2, img_san3, img_san4, img_san5,status, role) values
('Ngọc Khánh', '2004-02-02', 'M', '1859305738', 'concatre3@gmail.com', 'h', 'h', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRfA74jKoBCGxNadWXx6CI2uQiOrdOyh53LPQ&s','Đảo Xanh Pickleball','Đảo Xanh 3, Hoà Cường Bắc, Hải Châu, Đà Nẵng', 'https://uyenuong.net/wp-content/uploads/2022/06/dien-tich-san-5-nguoi-3.jpg','https://www.tapdoanconhantao.com/wp-content/uploads/2019/07/thu-nhap-cao-nho-kinh-doanh-san-bong-da-co-nhan-tao.jpg','https://thegioiconhantao.com.vn/wp-content/uploads/2016/11/dien1.jpg','https://golfstore.vn/images/pro/1_3165.jpg','https://foba.vn/wp-content/uploads/2020/09/Hinh-anh-%E2%80%93-2020-Hoan-Thanh-Cong-Trinh-San-Bong-Binh-Thuan-1.jpg',0,'C'),
('Đoàn Công Sơn', '2004-02-02', 'M', '1919191857', 'concatre4@gmail.com', 'e', 'e', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRfA74jKoBCGxNadWXx6CI2uQiOrdOyh53LPQ&s','Sân Bóng Đá Trưng Vương','560 Trưng Nữ Vương, Hòa Thuận Tây, Hải Châu, Đà Nẵng', 'https://uyenuong.net/wp-content/uploads/2022/06/dien-tich-san-5-nguoi-3.jpg','https://www.tapdoanconhantao.com/wp-content/uploads/2019/07/thu-nhap-cao-nho-kinh-doanh-san-bong-da-co-nhan-tao.jpg','https://thegioiconhantao.com.vn/wp-content/uploads/2016/11/dien1.jpg','https://golfstore.vn/images/pro/1_3165.jpg','https://foba.vn/wp-content/uploads/2020/09/Hinh-anh-%E2%80%93-2020-Hoan-Thanh-Cong-Trinh-San-Bong-Binh-Thuan-1.jpg',0,'C'),
('Trê', '2004-02-02', 'M', '0186947382', 'concatre1@gmail.com', 'f', 'f', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRfA74jKoBCGxNadWXx6CI2uQiOrdOyh53LPQ&s','Sân bóng rổ Jumpshots','02 Cách Mạng Tháng 8, Hoà Cường Nam, Hải Châu, Đà Nẵng', 'https://uyenuong.net/wp-content/uploads/2022/06/dien-tich-san-5-nguoi-3.jpg','https://www.tapdoanconhantao.com/wp-content/uploads/2019/07/thu-nhap-cao-nho-kinh-doanh-san-bong-da-co-nhan-tao.jpg','https://thegioiconhantao.com.vn/wp-content/uploads/2016/11/dien1.jpg','https://golfstore.vn/images/pro/1_3165.jpg','https://foba.vn/wp-content/uploads/2020/09/Hinh-anh-%E2%80%93-2020-Hoan-Thanh-Cong-Trinh-San-Bong-Binh-Thuan-1.jpg',0,'C'),
('Ngọc Sơn', '2004-02-02', 'M', '1750375945', 'concatre2@gmail.com', 'g', 'g', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRfA74jKoBCGxNadWXx6CI2uQiOrdOyh53LPQ&s','Sân Tập Golf DNC','99 Đường Xuân Thủy, Huyện Khuê Trung, Cẩm Lệ, Đà Nẵng', 'https://uyenuong.net/wp-content/uploads/2022/06/dien-tich-san-5-nguoi-3.jpg','https://www.tapdoanconhantao.com/wp-content/uploads/2019/07/thu-nhap-cao-nho-kinh-doanh-san-bong-da-co-nhan-tao.jpg','https://thegioiconhantao.com.vn/wp-content/uploads/2016/11/dien1.jpg','https://golfstore.vn/images/pro/1_3165.jpg','https://foba.vn/wp-content/uploads/2020/09/Hinh-anh-%E2%80%93-2020-Hoan-Thanh-Cong-Trinh-San-Bong-Binh-Thuan-1.jpg',0,'C'),
('Nigga', '2004-02-02', 'M', '1919191857', 'concatre4@gmail.com', 'e', 'e', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRfA74jKoBCGxNadWXx6CI2uQiOrdOyh53LPQ&s','Sân Bóng Đá Trưng Vương','560 Trưng Nữ Vương, Hòa Thuận Tây, Hải Châu, Đà Nẵng', 'https://uyenuong.net/wp-content/uploads/2022/06/dien-tich-san-5-nguoi-3.jpg','https://www.tapdoanconhantao.com/wp-content/uploads/2019/07/thu-nhap-cao-nho-kinh-doanh-san-bong-da-co-nhan-tao.jpg','https://thegioiconhantao.com.vn/wp-content/uploads/2016/11/dien1.jpg','https://golfstore.vn/images/pro/1_3165.jpg','https://foba.vn/wp-content/uploads/2020/09/Hinh-anh-%E2%80%93-2020-Hoan-Thanh-Cong-Trinh-San-Bong-Binh-Thuan-1.jpg',0,'C'),
('Vinh', '2004-02-02', 'M', '0186947382', 'concatre1@gmail.com', 'f', 'f', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRfA74jKoBCGxNadWXx6CI2uQiOrdOyh53LPQ&s','Sân bóng rổ Jumpshots','02 Cách Mạng Tháng 8, Hoà Cường Nam, Hải Châu, Đà Nẵng', 'https://uyenuong.net/wp-content/uploads/2022/06/dien-tich-san-5-nguoi-3.jpg','https://www.tapdoanconhantao.com/wp-content/uploads/2019/07/thu-nhap-cao-nho-kinh-doanh-san-bong-da-co-nhan-tao.jpg','https://thegioiconhantao.com.vn/wp-content/uploads/2016/11/dien1.jpg','https://golfstore.vn/images/pro/1_3165.jpg','https://foba.vn/wp-content/uploads/2020/09/Hinh-anh-%E2%80%93-2020-Hoan-Thanh-Cong-Trinh-San-Bong-Binh-Thuan-1.jpg',0,'C'),
('Rot', '2004-02-02', 'M', '1750375945', 'concatre2@gmail.com', 'g', 'g', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRfA74jKoBCGxNadWXx6CI2uQiOrdOyh53LPQ&s','Sân Tập Golf DNC','99 Đường Xuân Thủy, Huyện Khuê Trung, Cẩm Lệ, Đà Nẵng', 'https://uyenuong.net/wp-content/uploads/2022/06/dien-tich-san-5-nguoi-3.jpg','https://www.tapdoanconhantao.com/wp-content/uploads/2019/07/thu-nhap-cao-nho-kinh-doanh-san-bong-da-co-nhan-tao.jpg','https://thegioiconhantao.com.vn/wp-content/uploads/2016/11/dien1.jpg','https://golfstore.vn/images/pro/1_3165.jpg','https://foba.vn/wp-content/uploads/2020/09/Hinh-anh-%E2%80%93-2020-Hoan-Thanh-Cong-Trinh-San-Bong-Binh-Thuan-1.jpg',0,'C');
select * from users;
insert into loaiSan(loai_san_type) values
('Sân Bóng Đá'),
('Sân Cầu Lông'),
('Sân Bóng Chuyền'),
('Sân Bóng Rổ'),
('Sân Pickleball'),
('Sân Tenis'),
('Sân Golf'),
('Sân Bóng Bàn');

insert into size(size) values
('Sân 5'),
('Sân 6'),
('Sân 7'),
('Sân 11'),
('Sân tiêu chuẩn');
-- Chuyen viet
INSERT INTO san (uid, loai_san_id, vi_tri_san, size_id, img, is_approve) VALUES
(4, 1, '1A', 1, 'https://vecgroup.vn/upload_images/images/2021/12/09/kich-thuoc-san-bong-11-nguoi(1).png', 1),
(4, 1, '1B', 1, 'https://vecgroup.vn/upload_images/images/2021/12/09/kich-thuoc-san-bong-11-nguoi(1).png', 1),
(4, 1, '1C', 1, 'https://vecgroup.vn/upload_images/images/2021/12/09/kich-thuoc-san-bong-11-nguoi(1).png', 1),
(4, 1, '1D', 1, 'https://vecgroup.vn/upload_images/images/2021/12/09/kich-thuoc-san-bong-11-nguoi(1).png', 1),
(4, 1, '2A', 1, 'https://vecgroup.vn/upload_images/images/2021/12/09/kich-thuoc-san-bong-11-nguoi(1).png', 1),
(4, 1, '2B', 1, 'https://vecgroup.vn/upload_images/images/2021/12/09/kich-thuoc-san-bong-11-nguoi(1).png', 1),
(4, 1, '2C', 1, 'https://vecgroup.vn/upload_images/images/2021/12/09/kich-thuoc-san-bong-11-nguoi(1).png', 1),
(4, 1, '2D', 1, 'https://vecgroup.vn/upload_images/images/2021/12/09/kich-thuoc-san-bong-11-nguoi(1).png', 1),
(4, 1, '3A', 1, 'https://vecgroup.vn/upload_images/images/2021/12/09/kich-thuoc-san-bong-11-nguoi(1).png', 1),
(4, 1, '3B', 1, 'https://vecgroup.vn/upload_images/images/2021/12/09/kich-thuoc-san-bong-11-nguoi(1).png', 1),
(4, 1, '4A', 1, 'https://vecgroup.vn/upload_images/images/2021/12/09/kich-thuoc-san-bong-11-nguoi(1).png', 1),
(4, 1, '4B', 1, 'https://vecgroup.vn/upload_images/images/2021/12/09/kich-thuoc-san-bong-11-nguoi(1).png', 1),
(4, 1, '5A', 3, 'https://vecgroup.vn/upload_images/images/2021/12/09/kich-thuoc-san-bong-11-nguoi(1).png', 1);

-- tuyen son
INSERT INTO san (uid, loai_san_id, vi_tri_san, size_id, img, is_approve) VALUES
-- bong da
(2, 1, '5A', 1, 'https://vecgroup.vn/upload_images/images/2021/12/09/kich-thuoc-san-bong-11-nguoi(1).png', 1),
(2, 1, '5B', 1, 'https://vecgroup.vn/upload_images/images/2021/12/09/kich-thuoc-san-bong-11-nguoi(1).png', 1),
(2, 1, '5C', 1, 'https://vecgroup.vn/upload_images/images/2021/12/09/kich-thuoc-san-bong-11-nguoi(1).png', 1),
(2, 1, '5D', 1, 'https://vecgroup.vn/upload_images/images/2021/12/09/kich-thuoc-san-bong-11-nguoi(1).png', 1),
(2, 1, '5E', 1, 'https://vecgroup.vn/upload_images/images/2021/12/09/kich-thuoc-san-bong-11-nguoi(1).png', 1),
(2, 1, '6A', 2, 'https://vecgroup.vn/upload_images/images/2021/12/09/kich-thuoc-san-bong-11-nguoi(1).png', 1),
(2, 1, '6B', 2, 'https://vecgroup.vn/upload_images/images/2021/12/09/kich-thuoc-san-bong-11-nguoi(1).png', 1),
(2, 1, '6C', 2, 'https://vecgroup.vn/upload_images/images/2021/12/09/kich-thuoc-san-bong-11-nguoi(1).png', 1),
(2, 1, '6D', 2, 'https://vecgroup.vn/upload_images/images/2021/12/09/kich-thuoc-san-bong-11-nguoi(1).png', 1),
(2, 1, '7A', 3, 'https://vecgroup.vn/upload_images/images/2021/12/09/kich-thuoc-san-bong-11-nguoi(1).png', 1),
(2, 1, '7B', 3, 'https://vecgroup.vn/upload_images/images/2021/12/09/kich-thuoc-san-bong-11-nguoi(1).png', 1),
(2, 1, '7C', 3, 'https://vecgroup.vn/upload_images/images/2021/12/09/kich-thuoc-san-bong-11-nguoi(1).png', 1),
(2, 1, '7D', 3, 'https://vecgroup.vn/upload_images/images/2021/12/09/kich-thuoc-san-bong-11-nguoi(1).png', 1),
-- tennis
(2, 6, '1', 4, 'https://www.shutterstock.com/image-vector/big-tennis-court-top-view-260nw-2493769193.jpg', 1),
(2, 6, '2', 4, 'https://www.shutterstock.com/image-vector/big-tennis-court-top-view-260nw-2493769193.jpg', 1),
(2, 6, '3', 4, 'https://www.shutterstock.com/image-vector/big-tennis-court-top-view-260nw-2493769193.jpg', 1),
(2, 6, '4', 4, 'https://www.shutterstock.com/image-vector/big-tennis-court-top-view-260nw-2493769193.jpg', 1),
(2, 6, '5', 4, 'https://www.shutterstock.com/image-vector/big-tennis-court-top-view-260nw-2493769193.jpg', 1),
(2, 6, '6', 4, 'https://www.shutterstock.com/image-vector/big-tennis-court-top-view-260nw-2493769193.jpg', 1),
(2, 6, '7', 4, 'https://www.shutterstock.com/image-vector/big-tennis-court-top-view-260nw-2493769193.jpg', 1),
-- bong ro
(2, 4, '1', 4, 'https://bongrotuoitre.vn/wp-content/uploads/2020/07/kich-thuoc-san-bong-ro.jpg', 1),
(2, 4, '2', 4, 'https://bongrotuoitre.vn/wp-content/uploads/2020/07/kich-thuoc-san-bong-ro.jpg', 1);
SELECT * FROM san;


INSERT INTO Schedulebooking (san_id, start_time, end_time, status, price, booking_date)
VALUES 
(14, '07:00:00', '08:30:00', 'available', 100.00, '2024-10-07'),
(14, '08:30:00', '10:00:00', 'available', 100.00, '2024-10-07'),
(14, '10:00:00', '11:30:00', 'available', 100.00, '2024-10-07'),
(14, '11:30:00', '13:00:00', 'available', 100.00, '2024-10-07'),
(14, '13:00:00', '14:30:00', 'available', 100.00, '2024-10-07'),
(14, '14:30:00', '16:00:00', 'available', 100.00, '2024-10-07'),
(14, '16:00:00', '17:30:00', 'available', 100.00, '2024-10-07'),
(14, '17:30:00', '19:00:00', 'available', 100.00, '2024-10-07'),
(14, '19:00:00', '20:30:00', 'available', 100.00, '2024-10-07'),
(14, '20:30:00', '22:00:00', 'available', 100.00, '2024-10-07'),
(14, '07:00:00', '08:30:00', 'available', 300.00, '2024-10-08'),
(14, '08:30:00', '10:00:00', 'available', 400.00, '2024-10-08'),
(14, '10:00:00', '11:30:00', 'available', 500.00, '2024-10-08'),
(14, '11:30:00', '13:00:00', 'available', 600.00, '2024-10-08'),
(14, '13:00:00', '14:30:00', 'available', 700.00, '2024-10-08'),
(14, '14:30:00', '16:00:00', 'available', 800.00, '2024-10-08'),
(14, '16:00:00', '17:30:00', 'available', 900.00, '2024-10-08'),
(14, '17:30:00', '19:00:00', 'available', 1000.00, '2024-10-08'),
(14, '19:00:00', '20:30:00', 'available', 1100.00, '2024-10-08'),
(14, '20:30:00', '22:00:00', 'available', 1200.00, '2024-10-08');
select * from Schedulebooking where san_id = 15;
select * from san where san_id=15;
select * from users where uid =2;
ALTER TABLE Schedulebooking ADD CONSTRAINT unique_booking UNIQUE (san_id, start_time, end_time, booking_date);
DELETE FROM Schedulebooking;
SET SQL_SAFE_UPDATES = 0;
SELECT * FROM ScheduleBooking 
WHERE san_id = 14 
AND start_time = '00:12:00' 
AND end_time = '13:09:00' 
AND booking_date = '2025-10-09';

SELECT * FROM ScheduleBooking 
WHERE san_id = 35;


SELECT sb.Sbooking_id, sb.san_id, sb.start_time, sb.end_time, sb.status, sb.price, sb.booking_date
FROM Schedulebooking sb
JOIN san s ON sb.san_id = s.san_id
JOIN users u ON s.uid = u.uid
WHERE u.uid = 2;  -- Thay ? bằng id người dùng cụ thể


SELECT sb.Sbooking_id, sb.san_id, sb.start_time, sb.end_time, sb.status, sb.price, sb.booking_date
FROM Schedulebooking sb
JOIN san s ON sb.san_id = s.san_id
JOIN users u ON s.uid = u.uid
WHERE u.uid = 2          -- Thay ? bằng id người dùng cụ thể
-- AND sb.booking_date = '2024-10-07'  -- Thay ? bằng ngày cụ thể
AND sb.start_time = '14:30:00'    -- Thay ? bằng start_time cụ thể
AND sb.end_time = '16:00:00';     -- Thay ? bằng end_time cụ thể
