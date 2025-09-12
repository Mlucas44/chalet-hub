create table if not exists chalets(
  id bigserial primary key,
  name text not null,
  location text not null,
  price_per_night double precision not null
);
