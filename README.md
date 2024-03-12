# CurrencyExchange 
Проект "Обмен валют"

REST API для описания валют и обменных курсов. Позволяет просматривать и редактировать списки валют и обменных курсов, и совершать расчёт конвертации произвольных сумм из одной валюты в другую.

Веб-интерфейс для проекта не подразумевается.

## REST API

### Валюты

#### GET `/currencies`

Получение списка валют. Пример ответа:
```json
[
    {
        "id": 1,
        "name": "Russian Ruble",
        "code": "RUB",
        "sign": "₽"
    },
    {
        "id": 2,
        "name": "US Dollar",
        "code": "USD",
        "sign": "$"
    },
    {
        "id": 3,
        "name": "Euro",
        "code": "EUR",
        "sign": "€"
    }
]
```

HTTP коды ответов:
- Успех - 200
- Ошибка, база данных недоступна - 500

#### GET `/currency/USD`

Получение конкретной валюты. Пример ответа:
```json
{
    "id": 2,
    "name": "US Dollar",
    "code": "USD",
    "sign": "$"
}
```

HTTP коды ответов:
- Успех - 200
- Код валюты отсутствует в адресе - 400
- Валюта не найдена - 404
- Ошибка, база данных недоступна - 500

#### POST `/currencies`

Добавление новой валюты в базу. Данные передаются в теле запроса в виде полей формы (`x-www-form-urlencoded`). Поля формы - `name`, `code`, `sign`. Пример ответа - JSON представление вставленной в базу записи, включая её ID:
```json
{
    "id": 4,
    "name": "Chinese yuan",
    "code": "CNY",
    "sign": "¥"
}
```

HTTP коды ответов:
- Успех - 201
- Отсутствует нужное поле формы - 400
- Валюта с таким кодом уже существует - 409
- Ошибка, база данных недоступна - 500

### Обменные курсы

#### GET `/exchangeRates`

Получение списка всех обменных курсов. Пример ответа:
```json
[
    {
        "id": 1,
        "baseCurrency": {
            "id": 2,
            "name": "US Dollar",
            "code": "USD",
            "sign": "$"
        },
        "targetCurrency": {
            "id": 1,
            "name": "Russian Ruble",
            "code": "RUB",
            "sign": "₽"
        },
        "rate": 90.7493
    },
    {
        "id": 2,
        "baseCurrency": {
            "id": 3,
            "name": "Euro",
            "code": "EUR",
            "sign": "€"
        },
        "targetCurrency": {
            "id": 1,
            "name": "Russian Ruble",
            "code": "RUB",
            "sign": "₽"
        },
        "rate": 98.8767
    }
]
```

HTTP коды ответов:
- Успех - 200
- Ошибка, база данных недоступна - 500

#### GET `/exchangeRate/USDRUB`

Получение конкретного обменного курса. Валютная пара задаётся идущими подряд кодами валют в адресе запроса. Пример ответа:
```json
{
    "id": 1,
    "baseCurrency": {
        "id": 2,
        "name": "US Dollar",
        "code": "USD",
        "sign": "$"
    },
    "targetCurrency": {
        "id": 1,
        "name": "Russian Ruble",
        "code": "RUB",
        "sign": "₽"
    },
    "rate": 90.7493
}

```

HTTP коды ответов:
- Успех - 200
- Коды валют пары отсутствуют в адресе - 400
- Обменный курс для пары не найден - 404
- Ошибка, база данных недоступна - 500

#### POST `/exchangeRates`

Добавление нового обменного курса в базу. Данные передаются в теле запроса в виде полей формы (`x-www-form-urlencoded`). Поля формы - `baseCurrencyCode`, `targetCurrencyCode`, `rate`. Пример полей формы:
- `baseCurrencyCode` - CNY
- `targetCurrencyCode` - RUB
- `rate` - 12.5756

Пример ответа - JSON представление вставленной в базу записи, включая её ID:
```json
{
    "id": 3,
    "baseCurrency": {
        "id": 4,
        "name": "Chinese yuan",
        "code": "CNY",
        "sign": "¥"
    },
    "targetCurrency": {
        "id": 1,
        "name": "Russian Ruble",
        "code": "RUB",
        "sign": "₽"
    },
    "rate": 12.5756
}
```

HTTP коды ответов:
- Успех - 201
- Отсутствует нужное поле формы - 400
- Валютная пара с таким кодом уже существует - 409
- Одна (или обе) валюта из валютной пары не существует в БД - 404
- Ошибка, база данных недоступна - 500

#### PATCH `/exchangeRate/USDRUB`

Обновление существующего в базе обменного курса. Валютная пара задаётся идущими подряд кодами валют в адресе запроса. Данные передаются в теле запроса в виде полей формы (`x-www-form-urlencoded`). Единственное поле формы - `rate`.

Пример ответа - JSON представление обновлённой записи в базе данных, включая её ID:
```json
{
    "id": 2,
    "baseCurrency": {
        "id": 3,
        "name": "Euro",
        "code": "EUR",
        "sign": "€"
    },
    "targetCurrency": {
        "id": 1,
        "name": "Russian Ruble",
        "code": "RUB",
        "sign": "₽"
    },
    "rate": 98.8767
}

```

HTTP коды ответов:
- Успех - 200
- Отсутствует нужное поле формы - 400
- Валютная пара отсутствует в базе данных - 404
- Ошибка, база данных недоступна - 500

### Обмен валюты

#### GET `/exchange?from=BASE_CURRENCY_CODE&to=TARGET_CURRENCY_CODE&amount=$AMOUNT`

Расчёт перевода определённого количества средств из одной валюты в другую. Получение курса для обмена может пройти по одному из трёх сценариев.  
1. В таблице `exchange_rates` существует валютная пара **USD-RUB** - берём её курс.  
Пример запроса - GET `/exchange?from=USD&to=RUB&amount=10.50`.

Пример ответа:
```json
{
    "baseCurrency": {
        "id": 2,
        "name": "US Dollar",
        "code": "USD",
        "sign": "$"
    },
    "targetCurrency": {
        "id": 1,
        "name": "Russian Ruble",
        "code": "RUB",
        "sign": "₽"
    },
    "rate": 90.7493,
    "amount": 10.50,
    "convertedAmount": 952.87
}
```

2. В таблице `exchange_rates` существует валютная пара **USD-RUB** - берем её курс, и считаем обратный, чтобы получить **RUB-USD**.  
Пример запроса - GET `exchange?from=RUB&to=USD&amount=1500`.

Пример ответа:
```json
{
    "baseCurrency": {
        "id": 1,
        "name": "Russian Ruble",
        "code": "RUB",
        "sign": "₽"
    },
    "targetCurrency": {
        "id": 2,
        "name": "US Dollar",
        "code": "USD",
        "sign": "$"
    },
    "rate": 0.0110,
    "amount": 1500,
    "convertedAmount": 16.50
}
```
3. В таблице `exchange_rates` существуют валютные пары **USD-RUB** и **CNY-RUB** - вычисляем из этих курсов курс **USD-CNY**.
Пример запроса - GET `exchange?from=USD&to=CNY&amount=10.50`.

Пример ответа:
```json
{
    "baseCurrency": {
        "id": 2,
        "name": "US Dollar",
        "code": "USD",
        "sign": "$"
    },
    "targetCurrency": {
        "id": 4,
        "name": "Chinese yuan",
        "code": "CNY",
        "sign": "¥"
    },
    "rate": 7.2163,
    "amount": 10.50,
    "convertedAmount": 75.77
}
```

HTTP коды ответов:
- Успех - 200
- Отсутствует нужное поле формы - 400
- Валюты не найдены - 404
