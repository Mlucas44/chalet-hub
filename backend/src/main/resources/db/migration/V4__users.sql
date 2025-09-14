-- Table des utilisateurs
create table if not exists users (
  id bigserial primary key,
  email text not null unique,
  password text not null
);

-- Table des rôles (ElementCollection)
create table if not exists user_roles (
  user_id bigint not null references users(id) on delete cascade,
  role varchar(20) not null
);

create index if not exists idx_user_roles_user on user_roles(user_id);