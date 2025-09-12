create table if not exists bookings(
  id bigserial primary key,
  chalet_id bigint not null references chalets(id),
  start_date date not null,
  end_date date not null,
  nights int not null,
  subtotal numeric(10,2) not null,
  tps numeric(10,2) not null,
  tvq numeric(10,2) not null,
  total numeric(10,2) not null,
  version bigint
);

create index if not exists idx_bookings_chalet_dates
  on bookings(chalet_id, start_date, end_date);
