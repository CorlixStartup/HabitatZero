create table
    estufa (
        id bigint auto_increment primary key,
        capacidade_m2 double not null,
        localizacao varchar(200) not null,
        nome varchar(100) not null,
        status enum ('ATIVA', 'INATIVA', 'MANUTENCAO') not null,
        threshold_oxigenio_min double null,
        threshold_radiacao_max double null,
        threshold_temperatura_max double null,
        threshold_umidade_min double null
    );

create table
    alerta (
        id bigint auto_increment primary key,
        criado_em datetime (6) not null,
        mensagem varchar(255) not null,
        resolvido bit not null,
        resolvido_em datetime (6) null,
        severidade enum ('ATENCAO', 'CRITICO', 'EMERGENCIA') not null,
        tipo_sensor enum (
            'OXIGENIO',
            'RADIACAO_EXTERNA',
            'TEMPERATURA',
            'UMIDADE_SOLO'
        ) null,
        valor_registrado double null,
        estufa_id bigint not null,
        constraint FKdqekelnv6591oy2i0yqtum1qw foreign key (estufa_id) references estufa (id)
    );

create table
    colono (
        id bigint auto_increment primary key,
        cargo enum (
            'AGRONOMISTA',
            'COMANDANTE',
            'ENGENHEIRO',
            'MEDICO',
            'TECNICO'
        ) not null,
        email varchar(150) not null,
        nome varchar(100) not null,
        senha_hash varchar(255) not null,
        estufa_id bigint null,
        constraint uk_colono_email unique (email),
        constraint FKg2lwt5x99nrrobfxudx92uy8g foreign key (estufa_id) references estufa (id)
    );

create table
    planta (
        id bigint auto_increment primary key,
        data_plantio date not null,
        fase_crescimento enum (
            'COLHEITA',
            'CRESCIMENTO',
            'GERMINACAO',
            'MATURACAO',
            'SEMENTE'
        ) not null,
        nome_cientifico varchar(150) not null,
        nome_comum varchar(100) null,
        estufa_id bigint not null,
        constraint FKm27rv9geccvu2y1bw0b8nskrx foreign key (estufa_id) references estufa (id)
    );

create table
    sensor_ambiente (
        id bigint auto_increment primary key,
        timestamp datetime (6) not null,
        tipo_sensor enum (
            'OXIGENIO',
            'RADIACAO_EXTERNA',
            'TEMPERATURA',
            'UMIDADE_SOLO'
        ) not null,
        unidade enum ('CELSIUS', 'MSV_HORA', 'PERCENTUAL') not null,
        valor_leitura double not null,
        estufa_id bigint not null,
        constraint FKnj2dr69j9nwdd4muyrh3fraci foreign key (estufa_id) references estufa (id)
    );

create index idx_sensor_estufa_tipo on sensor_ambiente (estufa_id, tipo_sensor);

create index idx_sensor_timestamp on sensor_ambiente (timestamp);