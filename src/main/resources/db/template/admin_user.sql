---  这是和 Keycloak 匹配用的，优化点在于缩短openId 长度，也可以新增新字段用来表述
create table if not exists admin_user(
    id serial primary key,
    third_id text not null,
    created_at timestamptz not null default now()
);
create unique index admin_user_open_id on admin_user(third_id);
comment on table admin_user is '运营管理人员';
comment on column admin_user.third_id is 'Keycloak SSO OpenId';