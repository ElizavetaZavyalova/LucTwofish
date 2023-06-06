create table  if not exists chanels(
        chanel_id  INTEGER primary key,
        user_name TEXT,
        password BLOB
);
create table  if not exists files(
    file_id INTEGER primary key,
    file_name TEXT,
    chanel_id INTEGER,
    FOREIGN KEY(chanel_id) REFERENCES chanels(chanel_id)
);
create table  if not exists privateKeys(
            chanel_id  INTEGER primary key,
            user_name TEXT,
            d0 TEXT,
            d1 TEXT,
            d2 TEXT,
            d3 TEXT,
            p TEXT,
            q TEXT
);