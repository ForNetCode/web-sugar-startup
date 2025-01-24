-- write init sql here.
-- the function pgMigrate would do database migration.

-- create database db;

create table if not exists account
(
    id         serial primary key,
    open_id    text not null,
    union_id   text,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);
create unique index if not exists account_open_id_index on account (open_id);
create index if not exists account_union_id_index on account (union_id);

comment on table account is '账户';

create table if not exists user_info
(
    id         integer primary key,
    nickname   text,
    created_at timestamptz not null  default now(),
    updated_at timestamptz not null default now()
);

comment on table user_info is '用户信息';

create table if not exists "order"
(
    id              bigserial primary key,
    user_id         integer        not null,
    info            jsonb          not null,
    pay             jsonb          not null,
    ref_id          text           not null,
    address_snap_id integer        not null,
    total_amount    numeric(10, 2) not null,
    status          smallint       not null default 0,
    note            text           not null,
    created_at      timestamptz    not null default now(),
    updated_at      timestamptz    not null default now()
);

comment on column "order".info is '所有关于计算总价相关的信息';
comment on column "order".note is '订单备注';
comment on column "order".ref_id is '订单履约关联表';
comment on column "order".status is '订单状态：0: 待支付，1:支付完成，待履约，2: 完成，3:取消（用户取消，支付超时）';
