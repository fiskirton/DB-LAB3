create extension if not exists dblink;

create or replace function is_exists ()
returns bool as
$$
    declare
        db_name text := 'ds1_armory';
    begin
        if exists (select true from pg_database where datname=db_name)
        then
            return true;
        else
            return false;
        end if;
    end;
$$ language plpgsql;

create or replace function create_db (password text)
returns bool as
$body$
    declare
        db_name text := 'ds1_armory';
    begin
        if is_exists()
        then
            raise notice 'Database "%" already exists', db_name;
            return false;
        else
            perform dblink_exec (
                'dbname=' || current_database() || ' user=' || "current_user"() || ' password=' || password,
                'create database ' || quote_ident(db_name) ||
                ' with owner ' || quote_ident(current_user)
                );
            return true;
        end if;
    end
$body$ language plpgsql;

create or replace function drop_db (password text)
returns bool as
$body$
    declare
        db_name text := 'ds1_armory';
    begin
        if not is_exists()
        then
            raise notice 'Database "%" does not exist', db_name;
            return false;
        else
            perform dblink_exec (
                'dbname=' || current_database() || ' user=' || "current_user"() || ' password=' || password,
                'drop database ' || quote_ident(db_name)
            );
            return true;
        end if;
    end;
$body$ language plpgsql;