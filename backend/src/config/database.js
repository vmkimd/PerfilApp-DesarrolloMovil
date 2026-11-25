// Conexión a la base de datos relacional SQLite (módulo nativo node:sqlite)
const { DatabaseSync } = require('node:sqlite');
const path = require('path');
const { RUTA_BD } = require('./config');

const db = new DatabaseSync(path.join(__dirname, '..', '..', RUTA_BD));

// Tabla usuarios: la foto se guarda como BLOB dentro de la misma base de datos
db.exec(`
  CREATE TABLE IF NOT EXISTS usuarios (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    correo TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    foto BLOB,
    foto_tipo TEXT,
    fecha_registro TEXT NOT NULL DEFAULT (datetime('now', 'localtime'))
  );
`);

module.exports = db;
