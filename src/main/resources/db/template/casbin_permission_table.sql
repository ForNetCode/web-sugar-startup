-- from https://casbin.org/docs/adapters/
create table casbin_rule(
    id serial primary key,
    ptype text,
    v0 text,
    v1 text,
    v2 text,
    v3 text,
    v4 text,
    v5 text
    -- created_at timestamptz default now()
    -- updated_at timestamptz default now()
);