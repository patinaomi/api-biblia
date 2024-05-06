select * from tb_usuario;
select * from tb_versiculo;

drop table tb_usuario cascade constraints;
drop table tb_versiculo cascade constraints;


-- Criação da tabela Tb_Usuario
CREATE TABLE Tb_Usuario (
    id_user NUMBER GENERATED ALWAYS as IDENTITY(START with 1 INCREMENT by 1) PRIMARY KEY,
    nome VARCHAR(255) UNIQUE,
    email VARCHAR(255),
    senha VARCHAR(255),
    notificacoes CHAR(1),
    external_id VARCHAR(255)
);

-- Criação da tabela Tb_Versiculo
CREATE TABLE Tb_Versiculo (
    id_versiculo NUMBER GENERATED ALWAYS as IDENTITY(START with 1 INCREMENT by 1) PRIMARY KEY,
    livro VARCHAR(255),
    capitulo NUMBER,
    numero NUMBER,
    texto VARCHAR(400),
    data_registro TIMESTAMP,
    id_usuario NUMBER,
    FOREIGN KEY (id_usuario) REFERENCES Tb_Usuario (id_user)
);
