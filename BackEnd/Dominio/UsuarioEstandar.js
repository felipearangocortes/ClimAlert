
const Usuario = require('./Usuario');

class UsuarioEstandar extends Usuario {

    constructor(Email, Passwd) {

        super(Email, Passwd);
    }

    isAdmin() {
        return true;
    }

}

module.exports = UsuarioEstandar;