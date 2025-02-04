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
-- drop  table "orders";
create table if not exists orders
(
    id              bigserial primary key,
    user_id         integer        not null,
    info            jsonb          not null,
    pay             jsonb          not null,
    ref_id          text           not null,
    -- address_snap_id integer        not null,
    total_amount    numeric(10, 2) not null,
    status          smallint       not null default 0,
    note            text           not null,
    created_at      timestamptz    not null default now(),
    updated_at      timestamptz    not null default now()
);

comment on column orders.info is '所有关于计算总价相关的信息';
comment on column orders.note is '订单备注';
comment on column orders.ref_id is '订单履约关联表';
comment on column orders.status is '订单状态';


-- drop table refund;
create table if not exists refund(
  id bigserial primary key,
  order_id  bigint not null,
  pay jsonb not null,
  status smallint not null default 0,
  reason text,
  created_at  timestamptz  not null default now(),
  updated_at  timestamptz  not null default now()
);
create index if not exists refund_order_id_index on refund (order_id);

drop table audit;
create table if not exists audit(
    id bigserial primary key,
    module text not null,
    ref_id  bigint not null,
    action text not null,
    description text,
    created_at timestamptz not null default now()
);