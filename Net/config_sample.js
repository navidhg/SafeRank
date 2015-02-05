// Example config.js file to store connection details. Note how this module
// is called and used in the server code in safetyserver.js
var config = {};

config.postgres_user = "username";
config.postgres_password = "password";
config.server = "localhost";
config.database = "safetydata";

var getPostgresConnectionString = function() {
		return "postgres://" + config.postgres_user + ":" + config.postgres_password + "@" + config.server + "/" + config.database;
	};

exports.getPostgresConnectionString = getPostgresConnectionString;