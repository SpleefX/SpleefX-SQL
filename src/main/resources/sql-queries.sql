create table Statistics
(
    Player varchar not null constraint Statistics_pk primary key,
    Data varchar default '{}'
);

create unique index Statistics_Player_uindex
    on Statistics (Player);

