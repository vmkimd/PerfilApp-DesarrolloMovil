const express = require('express');
const multer = require('multer');
const auth = require('../middlewares/auth');
const perfilController = require('../controllers/perfilController');

const router = express.Router();

// memoryStorage: multer NO escribe la imagen en una carpeta del servidor
const subida = multer({
  storage: multer.memoryStorage(),
  limits: { fileSize: 5 * 1024 * 1024 },
  fileFilter: (req, file, cb) => {
    if (file.mimetype.startsWith('image/')) cb(null, true);
    else cb(new Error('Solo se permiten imágenes'));
  }
});

router.get('/', auth, perfilController.obtenerPerfil);
router.put('/foto', auth, subida.single('foto'), perfilController.actualizarFoto);
router.get('/foto', auth, perfilController.obtenerFoto);

module.exports = router;
