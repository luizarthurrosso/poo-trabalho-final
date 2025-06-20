DROP TABLE IF EXISTS public.tb_disciplina_professor;
DROP TABLE IF EXISTS public.tb_arquivos_processados;
DROP TABLE IF EXISTS public.tb_disciplinas;
DROP TABLE IF EXISTS public.tb_professores;
DROP TABLE IF EXISTS public.tb_fases;
DROP TABLE IF EXISTS public.tb_cursos;

CREATE TABLE public.tb_cursos (
    id SERIAL PRIMARY KEY,
    nome_curso VARCHAR(50),
    data_processamento DATE,
    sequencia_arquivo INTEGER,
    versao_layout VARCHAR(3)
);

CREATE TABLE public.tb_fases (
    id SERIAL PRIMARY KEY,
    nome_fase VARCHAR(7),
    curso_id INTEGER NOT NULL,
    CONSTRAINT fk_curso
        FOREIGN KEY(curso_id)
        REFERENCES public.tb_cursos(id)
        ON DELETE CASCADE
);

CREATE TABLE public.tb_professores (
    id SERIAL PRIMARY KEY,
    nome_professor VARCHAR(40),
    titulo_docente INTEGER
);

CREATE TABLE public.tb_disciplinas (
    id SERIAL PRIMARY KEY,
    codigo_disciplina VARCHAR(6),
    nome_disciplina VARCHAR(50),
    dia_semana INTEGER,
    fase_id INTEGER NOT NULL,
    CONSTRAINT fk_fase
        FOREIGN KEY(fase_id)
        REFERENCES public.tb_fases(id)
        ON DELETE CASCADE
);

CREATE TABLE public.tb_disciplina_professor (
    disciplina_id INTEGER NOT NULL,
    professor_id INTEGER NOT NULL,
    CONSTRAINT fk_disciplina
        FOREIGN KEY(disciplina_id)
        REFERENCES public.tb_disciplinas(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_professor
        FOREIGN KEY(professor_id)
        REFERENCES public.tb_professores(id)
        ON DELETE CASCADE,
    PRIMARY KEY (disciplina_id, professor_id)
);

CREATE TABLE public.tb_arquivos_processados (
    id SERIAL PRIMARY KEY,
    nome_arquivo VARCHAR(255) NOT NULL,
    hash_arquivo VARCHAR(64) NOT NULL,
    data_importacao TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT hash_unico UNIQUE (hash_arquivo)
);