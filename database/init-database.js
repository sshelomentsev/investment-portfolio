db._createDatabase("investment", {}, [{ username: "investment", passwd: "investment", active: true}]);
db._useDatabase("investment");

db._create("user");

db._create("currency");
db.currency.save({code: 'BTC', name: 'Bitcoin'});
db.currency.save({code: 'XRP', name: 'Ripple'});
db.currency.save({code: 'ETH', name: 'Ethereum'});
db.currency.save({code: 'DASH', name: 'Dash'});
db.currency.save({code: 'LTC', name: 'Litecoin'});
db.currency.save({code: 'USD', name: 'United States Dollar', type: 'fiat'});

db._create("transaction");
