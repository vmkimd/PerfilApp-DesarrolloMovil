// Controlador de autenticación: registro e inicio de sesión
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const db = require('../config/database');
const { JWT_SECRETO, JWT_EXPIRA } = require('../config/config');

const REGEX_CORREO = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

exports.registrar = (req, res) => {
  const nombre = (req.body.nombre || '').trim();
  const correo = (req.body.correo || '').trim().toLowerCase();
  const password = req.body.password || '';

  if (!nombre || !correo || !password) {
    return res.status(400).json({ ok: false, mensaje: 'Todos los campos son obligatorios' });
  }
  if (!REGEX_CORREO.test(correo)) {
    return res.status(400).json({ ok: false, mensaje: 'El correo no es válido' });
  }
  if (password.length < 6) {
    return res.status(400).json({ ok: false, mensaje: 'La contraseña debe tener al menos 6 caracteres' });
  }

  const existe = db.prepare('SELECT id FROM usuarios WHERE correo = ?').get(correo);
  if (existe) {
    return res.status(409).json({ ok: false, mensaje: 'Ese correo ya está registrado' });
  }

  const hash = bcrypt.hashSync(password, 10);
  db.prepare('INSERT INTO usuarios (nombre, correo, password_hash) VALUES (?, ?, ?)')
    .run(nombre, correo, hash);

  return res.status(201).json({ ok: true, mensaje: 'Usuario registrado correctamente' });
};

exports.login = (req, res) => {
  const correo = (req.body.correo || '').trim().toLowerCase();
  const password = req.body.password || '';

  if (!correo || !password) {
    return res.status(400).json({ ok: false, mensaje: 'Ingresa tu correo y contraseña' });
  }

  const usuario = db.prepare('SELECT id, nombre, correo, password_hash FROM usuarios WHERE correo = ?').get(correo);
  if (!usuario || !bcrypt.compareSync(password, usuario.password_hash)) {
    return res.status(401).json({ ok: false, mensaje: 'Correo o contraseña incorrectos' });
  }

  const token = jwt.sign({ id: usuario.id }, JWT_SECRETO, { expiresIn: JWT_EXPIRA });

  return res.json({
    ok: true,
    mensaje: 'Inicio de sesión correcto',
    token,
    nombre: usuario.nombre
  });
};
