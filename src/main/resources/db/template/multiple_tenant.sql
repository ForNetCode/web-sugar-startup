
create table if not exists tenant(
  id serial primary key,
  name text not null,
  status smallint not null default 0,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

comment on table tenant is '租户';
comment on column tenant.status is '租户状态，0: 正常, 1: 停用， 2～n: VIP0～n';

create table if not exists tenant_user(
   id serial primary key,
   -- mobile text not null,
   -- email text not null,
   -- account text not null,
   password text not null,
   created_at timestamptz not null default now(),
   updated_at timestamptz not null default now()
);

comment on table tenant_user is '租户于用户的关联关系';

create table if not exists tenant_user_link(
    id serial primary key,
    user_id int not null,
    tenant_id int not null,
    status smallint not null default 0,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);
comment on column tenant_user_link.status is '状态: 0:正常，1:禁用';
comment on table tenant_user_link is '租户与用户关联关系表';

-- permission ref: https://casbin.org/docs/rbac-with-domains

-- alter table ??? add column  tenant_id integer not null;


