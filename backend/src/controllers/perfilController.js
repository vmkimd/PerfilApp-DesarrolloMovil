// Controlador del perfil: datos del usuario y foto de perfil (BLOB)
const db = require('../config/database');

exports.obtenerPerfil = (req, res) => {
  const usuario = db.prepare(
    'SELECT id, nombre, correo, fecha_registro, (foto IS NOT NULL) AS tiene_foto FROM usuarios WHERE id = ?'
  ).get(req.usuarioId);

  if (!usuario) {
    return res.status(404).json({ ok: false, mensaje: 'Usuario no encontrado' });
  }

  return res.json({
    ok: true,
    usuario: {
      id: usuario.id,
      nombre: usuario.nombre,
      correo: usuario.correo,
      fechaRegistro: usuario.fecha_registro,
      tieneFoto: usuario.tiene_foto === 1
    }
  });
};

exports.actualizarFoto = (req, res) => {
  if (!req.file) {
    return res.status(400).json({ ok: false, mensaje: 'No se recibió ninguna imagen' });
  }

  // La imagen llega en memoria (req.file.buffer) y se guarda directo en la BD, no en disco
  db.prepare('UPDATE usuarios SET foto = ?, foto_tipo = ? WHERE id = ?')
    .run(req.file.buffer, req.file.mimetype, req.usuarioId);

  return res.json({ ok: true, mensaje: 'Foto de perfil actualizada' });
};

exports.obtenerFoto = (req, res) => {
  const fila = db.prepare('SELECT foto, foto_tipo FROM usuarios WHERE id = ?').get(req.usuarioId);

  if (!fila || !fila.foto) {
    return res.status(404).json({ ok: false, mensaje: 'El usuario no tiene foto de perfil' });
  }

  res.set('Content-Type', fila.foto_tipo || 'image/jpeg');
  res.set('Cache-Control', 'no-store');
  return res.send(Buffer.from(fila.foto));
};
