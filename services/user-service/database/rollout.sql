-- Установка таблиц сервиса юзеров
-- Длина id - 12 символов
begin;

create sequence main_id_sequence;
create function getnextid() returns char(8) as
    $$ declare
      str text :=  '0123456789abcdefghijklmnopqrstuvwxyz';
      val bigint;
      id_ text;
      mod int;
      begin
      val:=nextval('main_id_sequence');
      id_:='';
      while (length(id_) < 8) loop
        mod = val % 36;
        id_:=substring(str,mod+1,1)||id_;
        val = val / 36;
      end loop;
      id_:='user'||id_;
      return id_;
      return 'null';
      end;   $$
language plpgsql;

create function trigger_set_login() RETURNS trigger LANGUAGE plpgsql AS
    $$
    BEGIN
    IF new._login IS NULL
      THEN NEW._login = NEW.r_object_id;
    END IF;
    RETURN NEW;
    END;
    $$
language plpgsql;

-- тип юзера (тренер, клиент)
create table gm_user_type (
  _id char(12) primary key not null default value getnextid(),
  _name char(64) not null unique,
  _caption char(128) not null
);

-- Таблица с основной информацией о юзере.
-- Почта и телефон не обязательны, тк эта таблица будет использоваться для хранения не зареганных
-- клиентов. Они должны будут быть заполнены.
create table gm_user (
  _id char(12) primary key not null default value getnextid(),
  _login varchar(32) not null unique,
  _password varchar(128) not null,
  _creation_date timestamp not null default now(),
  _type char(12) not null references gm_user_type(_id),
  _name varchar(128) not null,
  _phone varchar(15),
  _email varchar(64),
  _active boolean default true,
  _auth_attempts int default 0 -- сколько было неудачных попыток авторизации
);
-- Тригер на создание. Если при insert не указан логин юзера - подставляем логин.
create trigger set_login_before_insert
  before insert on gm_user
  for each row
  execute function trigger_set_login();

create table gm_role (
  _id char(12) primary key not null default value getnextid(),
  _creation_date timestamp not null default now(),
  _name varchar(64) not null unique,
  _caption varchar(128) not null
);

create table gm_user_roles (
  _id char(12) primary key not null default value getnextid(),
  _creation_date timestamp not null default now(),
  _role_id char(12) not null references gm_role(_id),
  _user_id char(12) not null references gm_user(_id)
);

insert into gm_user_type (_name, _caption) values ('trainer', 'Тренер');

insert into gm_user (_login, _password, _name, _type)
  select
    'test_admin',
    '$2a$10$8vzgsIktNcMSE1/QU49jVeO1dVo2sJFFdHncZbN.QAFEhXovqSJA6'
    'Admin',
    t._name
  from gm_user_type where _name = 'trainer';

insert into gm_role (_name, _caption) values (
  'admin',
  'Админочка'
);

insert into gm_user_roles (_role_id, _user_id)
  select r._id, u._id from gm_role r, gm_user u where r._name = 'admin' and u._login = 'test_admin';

commit;