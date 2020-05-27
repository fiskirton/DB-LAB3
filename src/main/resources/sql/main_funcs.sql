create or replace function init_db ()
returns void as
$body$
    begin
        create table if not exists items (
            item_id serial primary key,
            item_title text
        );

        create unique index if not exists item_idx on items(lower(item_title));

        create table if not exists locations (
            location_id serial primary key,
            location_title text
        );

        create unique index if not exists location_idx on locations(lower(location_title));

        create table if not exists drop_types (
            drop_type_id serial primary key,
            drop_type text unique
        );

        create unique index if not exists drop_type_idx on drop_types(lower(drop_type));

        create table if not exists categories (
            category_id serial primary key,
            category_title text unique
        );

        create unique index if not exists category_idx on categories(lower(category_title));

        create table if not exists records (
            record_id text primary key,
            item int not null,
            location int not null,
            drop_type int not null,
            category int not null,
            base_price int,
            ng int not null check (ng > 0 and ng <= 5) default 1,
            ng_price int,
            primary key (item, base_price),
            foreign key (item) references items (item_id) on delete cascade on update cascade ,
            foreign key (location) references locations (location_id) on delete cascade on update cascade,
            foreign key (drop_type) references drop_types(drop_type_id) on delete cascade on update cascade,
            foreign key (category) references categories(category_id) on delete cascade on update cascade
        );

        create unique index if not exists item_price_idx on records(item, base_price);

        create or replace function set_ng_price()
        returns trigger as
        $$
            begin
                new.ng_price = new.base_price * new.ng;
                return new;
            end;
        $$ language plpgsql;

        drop trigger if exists ng_price_trigger on records;

        create trigger ng_price_trigger
        before insert or update on records
        for row execute procedure set_ng_price();
    end;
$body$ language plpgsql;

create or replace function get_records ()
returns table (
    id text,
    title text,
    location text,
    "drop type" text,
    category text,
    ng int,
    price int
) as
$body$
    begin
        return query
            select r.record_id, i.item_title, l.location_title, dt.drop_type, c.category_title, r.ng, r.ng_price
            from records r
            inner join items i on r.item = i.item_id
            inner join locations l on r.location = l.location_id
            inner join drop_types dt on r.drop_type = dt.drop_type_id
            inner join categories c on r.category = c.category_id;
    end;
$body$ language plpgsql;

create or replace function add_record(id int, title text, loc text, dt text, categ text, bp int, ng_val int)
returns bool as
$body$
    declare
            i_id int;
            l_id int;
            dt_id int;
            c_id int;
    begin

        if not exists(select item_title from items where lower(item_title) = lower(title))
        then
            insert into items (item_title) values (initcap(title));
        end if;

        if not exists(select location_title from locations where lower(location_title) = lower(loc))
        then
            insert into locations (location_title) values (initcap(loc));
        end if;

        if not exists(select drop_type from drop_types where lower(drop_type) = lower(dt))
        then
            insert into drop_types (drop_type) values (initcap(dt));
        end if;

        if not exists(select category_title from categories where lower(category_title) = lower(categ))
        then
            insert into categories (category_title) values (initcap(categ));
        end if;

        select item_id into i_id from items where item_title = initcap(title);
        select location_id into l_id from locations where location_title = initcap(loc);
        select drop_type_id into dt_id from drop_types where drop_type = initcap(dt);
        select category_id into c_id from categories where category_title = initcap(categ);

        insert into records (record_id, item, location, drop_type, category, base_price, ng)
        values (to_hex(id), i_id, l_id, dt_id, c_id, bp, ng_val);
        return true;
        exception
         when unique_violation then
             return false;
    end;
$body$ language plpgsql;

create or replace function edit_record(id text, title text, loc text, dt text, categ text, ng_val int)
returns bool as
$body$
    declare
            i_id int;
            l_id int;
            dt_id int;
            c_id int;
    begin

        if not exists(select item_title from items where lower(item_title) = lower(title))
        then
            insert into items (item_title) values (initcap(title));
        end if;

        if not exists(select location_title from locations where lower(location_title) = lower(loc))
        then
            insert into locations (location_title) values (initcap(loc));
        end if;

        if not exists(select drop_type from drop_types where lower(drop_type) = lower(dt))
        then
            insert into drop_types (drop_type) values (initcap(dt));
        end if;

        if not exists(select category_title from categories where lower(category_title) = lower(categ))
        then
            insert into categories (category_title) values (initcap(categ));
        end if;

        select item_id into i_id from items where item_title = initcap(title);
        select location_id into l_id from locations where location_title = initcap(loc);
        select drop_type_id into dt_id from drop_types where drop_type = initcap(dt);
        select category_id into c_id from categories where category_title = initcap(categ);

        update records
            set item = i_id,
                location = l_id,
                drop_type = dt_id,
                category = c_id,
                ng = ng_val
        where record_id like id;
        return true;

        exception
            when unique_violation then
                return false;
    end;
$body$ language plpgsql;

create or replace function edit_item(id int, new_title text)
returns bool as
$$
    begin
        update items
            set item_title = initcap(new_title)
        where item_id = id;
        return true;

    exception
        when unique_violation then
            return false;
    end;
$$ language plpgsql;


create or replace function edit_location(id int, new_title text)
returns bool as
$$
    begin
        update locations
            set location_title = initcap(new_title)
        where location_id = id;
        return true;

        exception
            when unique_violation then
                return false;
    end;
$$ language plpgsql;

create or replace function edit_drop_type(id int, new_title text)
returns bool as
$$
    begin
        update drop_types
            set drop_type = initcap(new_title)
        where drop_type_id = id;
        return true;

        exception
            when unique_violation then
                return false;
    end;
$$ language plpgsql;

create or replace function edit_category(id int, new_title text)
returns bool as
$$
    begin
        update categories
            set category_title = initcap(new_title)
        where category_id = id;
        return true;

        exception
            when unique_violation then
                return false;
    end;
$$ language plpgsql;

create or replace function get_items()
returns table (
    id int,
    title text
) as
$$
begin
    return query select * from items;
end;
$$ language plpgsql;

create or replace function get_locations()
returns table (
    id int,
    title text
) as
$$
    begin
       return query select * from locations;
    end;
$$ language plpgsql;

create or replace function get_drop_types()
returns table (
    id int,
    type text
) as
$$
    begin
       return query select * from drop_types;
    end;
$$ language plpgsql;

create or replace function get_categories()
returns table (
    id int,
    title text
) as
$$
    begin
       return query select * from categories;
    end;
$$ language plpgsql;

create or replace function truncate_all()
returns bool as
$$
    begin
        truncate records, items, locations, drop_types, categories restart identity cascade;
        return true;
    end;
$$ language plpgsql;

create or replace function truncate_records()
returns bool as
$$
    begin
        truncate records;
        return true;
    end;
$$ language plpgsql;

create or replace function truncate_items()
returns bool as
$$
    begin
        truncate items restart identity cascade;
        return true;
    end;
$$ language plpgsql;


create or replace function truncate_locations()
returns bool as
$$
    begin
        truncate locations restart identity cascade;
        return true;
    end;
$$ language plpgsql;

create or replace function truncate_drop_types()
returns bool as
$$
    begin
        truncate drop_types restart identity cascade;
        return true;
    end;
$$ language plpgsql;

create or replace function truncate_categories()
returns bool as
$$
    begin
        truncate categories restart identity cascade;
        return true;
    end;
$$ language plpgsql;

create or replace function find_records(item_param text default '', loc_param text default '', dt_param text default '', categ_param text = '')
returns table (
    id text,
    title text,
    location text,
    "drop type" text,
    category text,
    ng int,
    price int
) as
$$
    begin
        return query
            select r.record_id, i.item_title, l.location_title, dt.drop_type, c.category_title, r.ng, r.ng_price
            from records r
            inner join items i on r.item = i.item_id
            inner join locations l on r.location = l.location_id
            inner join drop_types dt on r.drop_type = dt.drop_type_id
            inner join categories c on r.category = c.category_id
            where (item_param = '' or lower(i.item_title) like concat('%', lower(item_param), '%')) and
                  (loc_param = '' or lower(l.location_title) = lower(loc_param)) and
                  (dt_param = '' or lower(dt.drop_type) = lower(dt_param)) and
                  (categ_param = '' or lower(c.category_title) = lower(categ_param));
    end;
$$ language plpgsql;

create or replace function get_record_by_id(rec_id int)
returns table (
    id text,
    title text,
    location text,
    "drop type" text,
    category text,
    ng int,
    price int
) as
$$
    begin
        return query
            select r.record_id, i.item_title, l.location_title, dt.drop_type, c.category_title, r.ng, r.ng_price
            from records r
            inner join items i on r.item = i.item_id
            inner join locations l on r.location = l.location_id
            inner join drop_types dt on r.drop_type = dt.drop_type_id
            inner join categories c on r.category = c.category_id
            where r.record_id = to_hex(rec_id);
    end;
$$ language plpgsql;

create or replace function delete_record(id int)
returns bool as
$$
    begin
        delete from records
        where record_id = to_hex(id);
        return true;
    end;
$$ language plpgsql;

create or replace function delete_item(title text)
returns bool as
$$
    begin
        delete from items
        where lower(item_title) = lower(title);
        return true;
    end;
$$ language plpgsql;

create or replace function delete_location(title text)
returns bool as
$$
    begin
        delete from locations
        where lower(location_title) = lower(title);
        return true;
    end;
$$ language plpgsql;

create or replace function delete_drop_type(title text)
returns bool as
$$
    begin
        delete from drop_types
        where lower(drop_type) = lower(title);
        return true;
    end;
$$ language plpgsql;

create or replace function delete_category(title text)
returns bool as
$$
    begin
        delete from categories
        where lower(category_title) = lower(title);
        return true;
    end;
$$ language plpgsql;

create or replace function delete_records(title text)
returns bool as
$$
    begin
        delete
        from records r
        using items i
        where r.item = i.item_id and
              lower(i.item_title) = lower(title);

        return true;
    end;
$$ language plpgsql;