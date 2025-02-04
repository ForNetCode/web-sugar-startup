create table if not exists product
(
    id         serial primary key,
    snap_id    int            not null,
    stock      int            not null default 0,
    status     smallint       not null default 0,
    category_id int  not  null,
    info       jsonb not null,
    created_at timestamptz    not null default now(),
    updated_at timestamptz    not null default now()
);

comment on table product is '商品';
comment on column product.name is '商品名称';
comment on column product.stock is '库存';
comment on column product.status is '0:草稿, 1:上架，2:下架';


create table if not exists product_snapshot
(
    id         serial primary key,
    name text not null,
    price      numeric(10, 2) not null,
    product_id integer     not null,
    data       jsonb       not null,
    created_at timestamptz not null default now()
);

create index if not exists product_snapshot_product_id_index on product_snapshot(product_id);

comment on table product_snapshot is '商品快照';
comment on column product_snapshot.data is '商品快照内容';

create table if not exists category
(
    id         serial primary key,
    name       text not null,
    level      smallint not null,
    parent_id  int,
    created_at timestamptz    not null default now(),
    updated_at timestamptz    not null default now(),
    foreign key (parent_id) references category (id)
);

create index if not exists category_parent_id_index on category(parent_id) where parent_id is not null;

comment on table category is '商品类目';
comment on column category.name is '类目名称';
comment on column category.parent_id is '父类目ID';
