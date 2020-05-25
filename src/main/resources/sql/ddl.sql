create extension if not exists dblink;;

create or replace function create_db ()
returns bool as
$func$
    declare
        db_name text := 'ds1_armory';
    begin
        if exists (select true from pg_database where datname=db_name)
        then
            raise notice 'Database "%" already exists', db_name;;
            return false;;
        else
            perform dblink_exec (
                        'dbname=' || current_database(),
                        'create database ' || quote_ident(db_name) ||
                        ' with owner ' || quote_ident(current_user)
                );;
            return true;;
        end if;;
    end;;
$func$ language plpgsql;;

create or replace function drop_db ()
returns bool as
$func$
    declare
        db_name text := 'ds1_armory';;
    begin
        if not exists (select true from pg_database where datname=db_name)
        then
            raise notice 'Database "%" does not exist', db_name;;
            return false;;
        else
            perform dblink_exec (
                        'dbname=' || current_database(),
                        'drop database ' || quote_ident(db_name)
                );;
            return true;;
        end if;;
    end;;
$func$ language plpgsql;;