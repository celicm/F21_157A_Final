drop table if exists ArchivedInvoices;
drop table if exists Invoices;
drop table if exists Booking;
drop table if exists Room;
drop table if exists RoomType;
drop table if exists Guest;

create table Guest(
    uID int AUTO_INCREMENT,
    name text NOT NULL,
    age int,
    PRIMARY KEY (uID)
);

create table RoomType(
    rType int,
    beds int,
    price int,
    PRIMARY KEY (rType)
);

create table Room(
    rnum int,
    rType int NOT NULL,
    PRIMARY KEY (rnum),
    FOREIGN KEY (rType) REFERENCES RoomType(rType) on update cascade
);

create table Booking(
    bID int AUTO_INCREMENT,
    uID int NOT NULL,
    rnum int NOT NULL,
    cid Date NOT NULL,
    cod Date NOT NULL,
    PRIMARY KEY (bID),
    FOREIGN KEY (rnum) REFERENCES Room(rnum) on update cascade,
    FOREIGN KEY (uID) REFERENCES Guest(uID) on delete cascade,
    CHECK (cid < cod)
);

#Archived relation 
create table Invoices(                             
    pID int AUTO_INCREMENT,
    bID int NOT NULL,
    total int NOT NULL,
    PRIMARY KEY (pID),
    FOREIGN KEY (bID) REFERENCES Booking(bID) on delete cascade,
    updatedOn timestamp not null
);

create table ArchivedInvoices(
    pID int NOT NULL,
    bID int NOT NULL,
    total int NOT NULL
);


drop trigger if exists UpdateBookingConflict;
delimiter //
create trigger UpdateBookingConflict
before update on Booking
for each row
begin
    IF EXISTS (select cid, cod from Booking where new.rnum = rnum and not(new.cid > cod or new.cod < cid)) THEN
    SIGNAL SQLSTATE '45000'
    SET MESSAGE_TEXT = 'The updating booking has check-in and check-out date conflict with existing booking';
    END IF;
end;
//
delimiter ;

drop trigger if exists InsertBookingConflict;
delimiter //
create trigger InsertBookingConflict
before insert on Booking
for each row
begin
    IF EXISTS (select cid, cod from Booking where new.rnum = rnum and not(new.cid > cod or new.cod < cid)) THEN
    SIGNAL SQLSTATE '45000'
    SET MESSAGE_TEXT = "The updating booking has check-in and check-out date conflict with existing booking";
    END IF;
end;
//
delimiter ;

/* Trigger to add invoice when a booking/reservation is created */
drop trigger if exists CreateInvoice;
delimiter //
create trigger CreateInvoice
after insert on Booking
for each row
begin
    declare roomPrice INT default 0;
    declare NumHours INT default 0;
    declare numDays INT default 0;
    set roomPrice = (select price from roomType where rType = (select rType from Room where rnum = new.rnum));
    set numDays = ABS(DATEDIFF(new.cid, new.cod)) + 1;
    insert into Invoices (bID, total, updatedOn) values (new.bID, roomPrice * numDays, current_timestamp);
end;
//
delimiter ;

/*
 * Trigger to add invoice when a booking/reservation is created
 */
drop trigger if exists UpdateInvoice;
delimiter //
create trigger UpdateInvoice
after update on Booking
for each row
begin
        declare roomPrice INT default 0;
        declare NumHours INT default 0;
        declare numDays INT default 0;
        set roomPrice = (select price from roomType where rType = (select rType from Room where rnum = new.rnum));
        set numDays = ABS(DATEDIFF(new.cid, new.cod)) + 1;
        update Invoices set total = roomPrice * numDays where bID = new.bID;
        update Invoices set updatedOn = current_timestamp where bID = new.bID;
end;
//
delimiter ;

insert into Guest values (1005,"Mike Tyson", 55);
insert into Guest values (957,"Jeff Bezos", 57);
insert into Guest values (115,"Tim Cook", 61);
insert into Guest values (527,"Jack Black", 52);
insert into Guest values (1001,"Arnold Schwarzenegger", 71);

insert into RoomType values (1, 1, 100);
insert into RoomType values (2, 2, 200);
insert into RoomType values (3, 2, 300);
insert into RoomType values (4, 3, 400);

insert into Room values (101, 1);
insert into Room values (102, 1);
insert into Room values (103, 1);
insert into Room values (104, 1);
insert into Room values (105, 1);
insert into Room values (201, 2);
insert into Room values (202, 2);
insert into Room values (203, 2);
insert into Room values (204, 2);
insert into Room values (205, 2);
insert into Room values (301, 3);
insert into Room values (302, 3);
insert into Room values (303, 3);
insert into Room values (304, 3);
insert into Room values (305, 3);
insert into Room values (401, 4);
insert into Room values (402, 4);
insert into Room values (403, 4);
insert into Room values (404, 4);
insert into Room values (405, 4);

insert into Booking values (121, 527, 203, '2021-11-01', '2021-11-05'); 
insert into Booking values (105, 1001, 305, '2021-11-19', '2021-12-06'); 
insert into Booking values (842, 115, 404, '2021-11-04', '2021-11-15'); 
insert into Booking values (19, 957, 102, '2021-10-01', '2021-10-29'); 
insert into Booking values (529, 1005, 302, '2021-12-01', '2021-12-10'); 

Drop procedure if exists ArchInvoice;
DELIMITER // 
Create procedure ArchInvoice(IN ctime timestamp)
Begin
    Insert into ArchivedInvoices(pId, bId, total)
        select pID, bID, total from Invoices
        Where TIMESTAMPDIFF(DAY, updatedOn, ctime) > 14 ;
    Delete from Invoices where pID in (select * from (select pID from Invoices
        Where TIMESTAMPDIFF(DAY, updatedOn, ctime) > 14)temp) ;
END; // 
DELIMITER ;



