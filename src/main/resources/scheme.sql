-- currency
drop table if exists public.currency cascade;
create table if not exists public.currency
(
    id   serial                 not null primary key,
    name character varying(100) not null,
    code character varying(3)   not null,
    sign character varying(2)   null
);
create unique index if not exists UIX_currency_code on public.currency (code);

-- exchange_rates
drop table if exists public.exchange_rates cascade;
create table if not exists public.exchange_rates
(
    id                 serial         not null primary key,
    base_currency_id   integer        not null references public.currency (id),
    target_currency_id integer        not null references public.currency (id),
    rate               decimal(10, 4) not null
);
create unique index if not exists UIX_exchange_rates on public.exchange_rates (base_currency_id, target_currency_id);