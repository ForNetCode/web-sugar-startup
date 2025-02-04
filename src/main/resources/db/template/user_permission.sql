--  role permission ref to Cloud Policy ， this would to cached
create table roles(
                      id serial primary key,
                      name text,
                      strategy text[] not null,
                      created_at timestamp without time zone
);

create table statements(
                           id serial primary key, -- resource,
                           name text not null,
                           description text,
                           action text[] not null,
                           effect smallint not null,
    -- resource text[],
                           created_at timestamp without time zone
);


create table user_statements(
                                id serial primary key,
                                user_id int not null,
                                ref_id jsonb[] not null, -- [{type:role|statement, ref_id:$id, resource_ids:[]}]
                                created_at timestamp without time zone,
                                updated_at timestamp without time zone
);

create unlogged table user_statements_cache(
    user_id int not null,
    action text not null,
    resource text[],
    effect smallint not null,
    created_at timestamp without time zone
);

create unique index user_statements_cache_index on user_statements_cache(user_id, action);