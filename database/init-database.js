db._createDatabase("investment", {}, [{ username: "investment", passwd: "investment", active: true}]);
db._useDatabase("investment");

db._create("user");

db._create("cryptoCurrency");
db.cryptoCurrency.save({code: 'BTC', name: 'Bitcoin'});
db.cryptoCurrency.save({code: 'XRP', name: 'Ripple'});
db.cryptoCurrency.save({code: 'ETH', name: 'Ethereum'});
db.cryptoCurrency.save({code: 'DASH', name: 'Dash'});
db.cryptoCurrency.save({code: 'LTC', name: 'Litecoin'});

db._create("fiatCurrency");
db.fiatCurrency.save({code: 'USD', name: 'United States Dollar'})

db._create("transaction");
db._create("balance");
