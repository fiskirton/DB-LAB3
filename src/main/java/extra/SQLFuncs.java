package extra;

public class SQLFuncs {
	public static final String HOST_FUNCS =
			"create extension if not exists dblink;\n" +
			"\n" +
			"create or replace function is_exists ()\n" +
			"returns bool as\n" +
			"$$\n" +
			"    declare\n" +
			"        db_name text := 'ds1_armory';\n" +
			"    begin\n" +
			"        if exists (select true from pg_database where datname=db_name)\n" +
			"        then\n" +
			"            return true;\n" +
			"        else\n" +
			"            return false;\n" +
			"        end if;\n" +
			"    end;\n" +
			"$$ language plpgsql;\n" +
			"\n" +
			"create or replace function create_db ()\n" +
			"returns bool as\n" +
			"$body$\n" +
			"    declare\n" +
			"        db_name text := 'ds1_armory';\n" +
			"    begin\n" +
			"        if is_exists()\n" +
			"        then\n" +
			"            raise notice 'Database \"%\" already exists', db_name;\n" +
			"            return false;\n" +
			"        else\n" +
			"            perform dblink_exec (\n" +
			"                'dbname=' || current_database(),\n" +
			"                'create database ' || quote_ident(db_name) ||\n" +
			"                ' with owner ' || quote_ident(current_user)\n" +
			"                );\n" +
			"            return true;\n" +
			"        end if;\n" +
			"    end\n" +
			"$body$ language plpgsql;\n" +
			"\n" +
			"create or replace function drop_db ()\n" +
			"returns bool as\n" +
			"$body$\n" +
			"    declare\n" +
			"        db_name text := 'ds1_armory';\n" +
			"    begin\n" +
			"        if not is_exists()\n" +
			"        then\n" +
			"            raise notice 'Database \"%\" does not exist', db_name;\n" +
			"            return false;\n" +
			"        else\n" +
			"            perform dblink_exec (\n" +
			"                'dbname=' || current_database(),\n" +
			"                'drop database ' || quote_ident(db_name)\n" +
			"            );\n" +
			"            return true;\n" +
			"        end if;\n" +
			"    end;\n" +
			"$body$ language plpgsql;";
	
	public static final String MAIN_FUNCS =
			"create or replace function init_db ()\n" +
			"returns void as\n" +
			"$body$\n" +
			"    begin\n" +
			"        create table if not exists locations (\n" +
			"            location_id serial primary key,\n" +
			"            location_title text\n" +
			"        );\n" +
			"\n" +
			"        create unique index if not exists location_idx on locations(lower(location_title));\n" +
			"\n" +
			"        create table if not exists drop_types (\n" +
			"            drop_type_id serial primary key,\n" +
			"            drop_type text unique\n" +
			"        );\n" +
			"\n" +
			"        create unique index if not exists drop_type_idx on drop_types(lower(drop_type));\n" +
			"\n" +
			"        create table if not exists categories (\n" +
			"            category_id serial primary key,\n" +
			"            category_title text unique\n" +
			"        );\n" +
			"\n" +
			"        create unique index if not exists category_idx on categories(lower(category_title));\n" +
			"\n" +
			"        create table if not exists items (\n" +
			"            item_id text primary key,\n" +
			"            item_title text not null,\n" +
			"            location int not null,\n" +
			"            drop_type int not null,\n" +
			"            category int not null,\n" +
			"            base_price int not null,\n" +
			"            ng int not null check ( ng <= 5 ) default 1,\n" +
			"            ng_price int,\n" +
			"            foreign key (location) references locations (location_id) on delete cascade on update cascade,\n" +
			"            foreign key (drop_type) references drop_types(drop_type_id) on delete cascade on update cascade,\n" +
			"            foreign key (category) references categories(category_id) on delete cascade on update cascade\n" +
			"        );\n" +
			"        create index if not exists item_idx on items (lower(item_title));\n" +
			"\n" +
			"        create or replace function set_ng_price()\n" +
			"        returns trigger as\n" +
			"        $$\n" +
			"            begin\n" +
			"                new.ng_price = new.base_price * new.ng;\n" +
			"                return new;\n" +
			"            end;\n" +
			"        $$ language plpgsql;\n" +
			"\n" +
			"        drop trigger if exists ng_price_trigger on items;\n" +
			"\n" +
			"        create trigger ng_price_trigger\n" +
			"        before insert or update on items\n" +
			"        for row execute procedure set_ng_price();\n" +
			"    end;\n" +
			"$body$ language plpgsql;" +
			"\n" +
			"create or replace function get_items ()\n" +
			"returns table (\n" +
			"    id text,\n" +
			"    title text,\n" +
			"    location text,\n" +
			"    \"drop type\" text,\n" +
			"    category text,\n" +
			"    ng int,\n" +
			"    price int\n" +
			") as\n" +
			"$body$\n" +
			"    begin\n" +
			"        return query\n" +
			"            select i.item_id, i.item_title, l.location_title, dt.drop_type, c.category_title, i.ng, i.ng_price\n" +
			"            from items i\n" +
			"            inner join locations l on i.location = l.location_id\n" +
			"            inner join drop_types dt on i.drop_type = dt.drop_type_id\n" +
			"            inner join categories c on i.category = c.category_id;\n" +
			"    end;\n" +
			"$body$ language plpgsql;\n" +
			"\n" +
			"create or replace function add_item(id int, title text, loc text, dt text, categ text, bp int, ng_val int)\n" +
			"returns bool as\n" +
			"$body$\n" +
			"    declare\n" +
			"            l_id int;\n" +
			"            dt_id int;\n" +
			"            c_id int;\n" +
			"    begin\n" +
			"\n" +
			"        if not exists(select location_title from locations where lower(location_title) = lower(loc))\n" +
			"        then\n" +
			"            insert into locations (location_title) values (initcap(loc));\n" +
			"        end if;\n" +
			"\n" +
			"        if not exists(select drop_type from drop_types where lower(drop_type) = lower(dt))\n" +
			"        then\n" +
			"            insert into drop_types (drop_type) values (initcap(dt));\n" +
			"        end if;\n" +
			"\n" +
			"        if not exists(select category_title from categories where lower(category_title) = lower(categ))\n" +
			"        then\n" +
			"            insert into categories (category_title) values (initcap(categ));\n" +
			"        end if;\n" +
			"\n" +
			"        select location_id into l_id from locations where location_title = initcap(loc);\n" +
			"        select drop_type_id into dt_id from drop_types where drop_type = initcap(dt);\n" +
			"        select category_id into c_id from categories where category_title = initcap(categ);\n" +
			"\n" +
			"        insert into items (item_id, item_title, location, drop_type, category, base_price, ng)\n" +
			"        values (to_hex(id), initcap(title), l_id, dt_id, c_id, bp, ng_val);\n" +
			"        return true;\n" +
			"        exception\n" +
			"         when unique_violation then\n" +
			"             return false;\n" +
			"    end;\n" +
			"$body$ language plpgsql;\n" +
			"\n" +
			"create or replace function edit_item(id text, title text, loc text, dt text, categ text, ng_val int)\n" +
			"returns bool as\n" +
			"$body$\n" +
			"    declare\n" +
			"            l_id int;\n" +
			"            dt_id int;\n" +
			"            c_id int;\n" +
			"    begin\n" +
			"\n" +
			"        if not exists(select location_title from locations where lower(location_title) = lower(loc))\n" +
			"        then\n" +
			"            insert into locations (location_title) values (initcap(loc));\n" +
			"        end if;\n" +
			"\n" +
			"        if not exists(select drop_type from drop_types where lower(drop_type) = lower(dt))\n" +
			"        then\n" +
			"        insert into drop_types (drop_type) values (initcap(dt));\n" +
			"        end if;\n" +
			"\n" +
			"        if not exists(select category_title from categories where lower(category_title) = lower(categ))\n" +
			"        then\n" +
			"        insert into categories (category_title) values (initcap(categ));\n" +
			"        end if;\n" +
			"\n" +
			"        select location_id into l_id from locations where location_title = initcap(loc);\n" +
			"        select drop_type_id into dt_id from drop_types where drop_type = initcap(dt);\n" +
			"        select category_id into c_id from categories where category_title = initcap(categ);\n" +
			"\n" +
			"        update items\n" +
			"            set item_title = initcap(title),\n" +
			"                location = l_id,\n" +
			"                drop_type = dt_id,\n" +
			"                category = c_id,\n" +
			"                ng = ng_val\n" +
			"        where item_id like id;\n" +
			"        return true;\n" +
			"\n" +
			"        exception\n" +
			"            when unique_violation then\n" +
			"                return false;\n" +
			"    end;\n" +
			"$body$ language plpgsql;\n" +
			"\n" +
			"create or replace function edit_location(id int, new_title text)\n" +
			"returns bool as\n" +
			"$$\n" +
			"    begin\n" +
			"        update locations\n" +
			"            set location_title = initcap(new_title)\n" +
			"        where location_id = id;\n" +
			"        return true;\n" +
			"\n" +
			"        exception\n" +
			"            when unique_violation then\n" +
			"                return false;\n" +
			"    end;\n" +
			"$$ language plpgsql;\n" +
			"\n" +
			"create or replace function edit_drop_type(id int, new_title text)\n" +
			"returns bool as\n" +
			"$$\n" +
			"    begin\n" +
			"        update drop_types\n" +
			"            set drop_type = initcap(new_title)\n" +
			"        where drop_type_id = id;\n" +
			"        return true;\n" +
			"\n" +
			"        exception\n" +
			"            when unique_violation then\n" +
			"                return false;\n" +
			"    end;\n" +
			"$$ language plpgsql;\n" +
			"\n" +
			"create or replace function edit_category(id int, new_title text)\n" +
			"returns bool as\n" +
			"$$\n" +
			"    begin\n" +
			"        update categories\n" +
			"            set category_title = initcap(new_title)\n" +
			"        where category_id = id;\n" +
			"        return true;\n" +
			"\n" +
			"        exception\n" +
			"            when unique_violation then\n" +
			"                return false;\n" +
			"    end;\n" +
			"$$ language plpgsql;\n" +
			"\n" +
			"create or replace function get_locations()\n" +
			"returns table (\n" +
			"    id int,\n" +
			"    title text\n" +
			") as\n" +
			"$$\n" +
			"    begin\n" +
			"       return query select * from locations;\n" +
			"    end;\n" +
			"$$ language plpgsql;\n" +
			"\n" +
			"create or replace function get_drop_types()\n" +
			"returns table (\n" +
			"    id int,\n" +
			"    type text\n" +
			") as\n" +
			"$$\n" +
			"    begin\n" +
			"       return query select * from drop_types;\n" +
			"    end;\n" +
			"$$ language plpgsql;\n" +
			"\n" +
			"create or replace function get_categories()\n" +
			"returns table (\n" +
			"    id int,\n" +
			"    title text\n" +
			") as\n" +
			"$$\n" +
			"    begin\n" +
			"       return query select * from categories;\n" +
			"    end;\n" +
			"$$ language plpgsql;\n" +
			"\n" +
			"create or replace function truncate_all()\n" +
			"returns bool as\n" +
			"$$\n" +
			"    begin\n" +
			"        truncate items, locations, drop_types, categories restart identity cascade;\n" +
			"        return true;\n" +
			"    end;\n" +
			"$$ language plpgsql;\n" +
			"\n" +
			"create or replace function truncate_items()\n" +
			"returns bool as\n" +
			"$$\n" +
			"    begin\n" +
			"        truncate items;\n" +
			"        return true;\n" +
			"    end;\n" +
			"$$ language plpgsql;\n" +
			"\n" +
			"create or replace function truncate_locations()\n" +
			"returns bool as\n" +
			"$$\n" +
			"    begin\n" +
			"        truncate locations restart identity cascade;\n" +
			"        return true;\n" +
			"    end;\n" +
			"$$ language plpgsql;\n" +
			"\n" +
			"create or replace function truncate_drop_types()\n" +
			"returns bool as\n" +
			"$$\n" +
			"    begin\n" +
			"        truncate drop_types restart identity cascade;\n" +
			"        return true;\n" +
			"    end;\n" +
			"$$ language plpgsql;\n" +
			"\n" +
			"create or replace function truncate_categories()\n" +
			"returns bool as\n" +
			"$$\n" +
			"    begin\n" +
			"        truncate categories restart identity cascade;\n" +
			"        return true;\n" +
			"    end;\n" +
			"$$ language plpgsql;\n" +
			"\n" +
			"create or replace function find_items(item_param text default '', loc_param text default '', dt_param text default '', categ_param text = '')\n" +
			"returns table (\n" +
			"    id text,\n" +
			"    title text,\n" +
			"    location text,\n" +
			"    \"drop type\" text,\n" +
			"    category text,\n" +
			"    ng int,\n" +
			"    price int\n" +
			") as\n" +
			"$$\n" +
			"    begin\n" +
			"        return query\n" +
			"            select i.item_id, i.item_title, l.location_title, dt.drop_type, c.category_title, i.ng, i.ng_price\n" +
			"            from items i\n" +
			"            inner join locations l on i.location = l.location_id\n" +
			"            inner join drop_types dt on i.drop_type = dt.drop_type_id\n" +
			"            inner join categories c on i.category = c.category_id\n" +
			"            where (item_param = '' or lower(i.item_title) like concat('%', lower(item_param), '%')) and\n" +
			"                  (loc_param = '' or lower(l.location_title) = lower(loc_param)) and\n" +
			"                  (dt_param = '' or lower(dt.drop_type) = lower(dt_param)) and\n" +
			"                  (categ_param = '' or lower(c.category_title) = lower(categ_param));\n" +
			"    end;\n" +
			"$$ language plpgsql;\n" +
			"\n" +
			"create or replace function get_item_by_id(itId int)\n" +
			"returns table (\n" +
			"    id text,\n" +
			"    title text,\n" +
			"    location text,\n" +
			"    \"drop type\" text,\n" +
			"    category text,\n" +
			"    ng int,\n" +
			"    price int\n" +
			") as\n" +
			"$$\n" +
			"    begin\n" +
			"        return query\n" +
			"            select i.item_id, i.item_title, l.location_title, dt.drop_type, c.category_title, i.ng, i.ng_price\n" +
			"            from items i\n" +
			"            inner join locations l on i.location = l.location_id\n" +
			"            inner join drop_types dt on i.drop_type = dt.drop_type_id\n" +
			"            inner join categories c on i.category = c.category_id\n" +
			"            where i.item_id = to_hex(itId);\n" +
			"    end;\n" +
			"$$ language plpgsql;\n" +
			"\n" +
			"create or replace function delete_item(id int)\n" +
			"returns bool as\n" +
			"$$\n" +
			"    begin\n" +
			"        delete from items\n" +
			"        where item_id = to_hex(id);\n" +
			"        return true;\n" +
			"    end;\n" +
			"$$ language plpgsql;\n" +
			"\n" +
			"create or replace function delete_location(title text)\n" +
			"returns bool as\n" +
			"$$\n" +
			"    begin\n" +
			"        delete from locations\n" +
			"        where lower(location_title) = lower(title);\n" +
			"        return true;\n" +
			"    end;\n" +
			"$$ language plpgsql;\n" +
			"\n" +
			"create or replace function delete_drop_type(title text)\n" +
			"returns bool as\n" +
			"$$\n" +
			"    begin\n" +
			"        delete from drop_types\n" +
			"        where lower(drop_type) = lower(title);\n" +
			"        return true;\n" +
			"    end;\n" +
			"$$ language plpgsql;\n" +
			"\n" +
			"create or replace function delete_category(title text)\n" +
			"returns bool as\n" +
			"$$\n" +
			"    begin\n" +
			"        delete from categories\n" +
			"        where lower(category_title) = lower(title);\n" +
			"        return true;\n" +
			"    end;\n" +
			"$$ language plpgsql;\n" +
			"\n" +
			"create or replace function delete_items(title text)\n" +
			"returns bool as\n" +
			"$$\n" +
			"    begin\n" +
			"        delete from items\n" +
			"        where lower(item_title) = lower(title);\n" +
			"        return true;\n" +
			"    end;\n" +
			"$$ language plpgsql;";
}
