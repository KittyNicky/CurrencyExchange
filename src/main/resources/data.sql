-- currency
INSERT INTO public.currency (name, code, sign) VALUES ('Russian Ruble', 'RUB' ,'₽');
INSERT INTO public.currency (name, code, sign) VALUES ('US Dollar', 'USD' ,'$');
INSERT INTO public.currency (name, code, sign) VALUES ('Euro', 'EUR' ,'€');

-- exchange_rates: https://www.cbr.ru/scripts/XML_daily.asp
-- USD-RUB
INSERT INTO public.exchange_rates (base_currency_id, target_currency_id, rate) VALUES (2, 1, 90.7493);
-- EUR-RUB
INSERT INTO public.exchange_rates (base_currency_id, target_currency_id, rate) VALUES (3, 1, 98.8767);