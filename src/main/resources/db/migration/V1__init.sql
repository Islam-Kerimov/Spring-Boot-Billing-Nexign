CREATE TABLE IF NOT EXISTS tariff
(
    id               SERIAL PRIMARY KEY,
    uuid             VARCHAR(2) UNIQUE  NOT NULL,
    name             VARCHAR(64) UNIQUE NOT NULL,
    fix_min          INT,
    fix_price        NUMERIC(5, 2),
    first_min        INT,
    first_price      NUMERIC(5, 2),
    minute_price     NUMERIC(5, 2),
    incoming_inside  BOOLEAN,
    outgoing_inside  BOOLEAN,
    incoming_another BOOLEAN,
    outgoing_another BOOLEAN,
    monetary_unit    VARCHAR(64) DEFAULT 'rubles',
    redirect         VARCHAR(2),
    operator         VARCHAR(32)        NOT NULL
);

INSERT INTO tariff (uuid, name, fix_min, fix_price, first_min, first_price, minute_price, incoming_inside,
                    outgoing_inside, incoming_another, outgoing_another, redirect, operator)
VALUES ('06', 'Безлимит 300', 300, 100, null, null, 1, null, null, null, null, null, 'Ромашка'),
       ('03', 'Поминутный', null, null, null, null, 1.5, null, null, null, null, null, 'Ромашка'),
       ('11', 'Обычный', null, null, 100, 0.5, null, true, null, true, null, '03', 'Ромашка'),
       ('82', 'Х', null, null, null, null, null, true, true, null, null, '03', 'Ромашка');

CREATE TABLE IF NOT EXISTS operator
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(64) UNIQUE NOT NULL
);

INSERT INTO operator(name)
VALUES ('Ромашка'),
       ('Березка');

CREATE TABLE IF NOT EXISTS subscriber
(
    id           SERIAL PRIMARY KEY,
    phone_number VARCHAR(11) UNIQUE           NOT NULL,
    tariff_id    INT REFERENCES tariff (id)   NOT NULL,
    balance      NUMERIC(9, 2) DEFAULT 0.00,
    operator_id  INT REFERENCES operator (id) NOT NULL
);

INSERT INTO subscriber(phone_number, tariff_id, balance, operator_id)
VALUES ('79181234567', 1, 400, 1),
       ('79181234568', 1, 400, 1),
       ('79181234569', 1, 400, 1),
       ('79181234570', 1, 400, 1),
       ('79181234571', 1, 400, 1),
       ('79181234572', 1, 400, 1),
       ('79181234573', 1, 400, 1),
       ('79181234574', 2, 400, 1),
       ('79181234575', 2, 400, 1),
       ('79181234576', 2, 400, 1),
       ('79181234577', 2, 400, 1),
       ('79181234578', 3, 400, 1),
       ('79181234579', 3, 400, 1),
       ('79181234580', 3, 400, 1),
       ('79181234581', 3, 400, 1),
       ('79181234582', 3, 400, 1),
       ('79181234583', 3, 400, 1),
       ('79181234584', 3, 400, 1),
       ('79181234585', 3, 400, 1),
       ('79181234586', 3, 400, 1),
       ('79181234587', 3, 400, 1),
       ('79181234588', 3, 400, 1),
       ('79181234589', 3, 400, 1),
       ('79181234590', 4, 400, 1),
       ('79181234591', 4, 400, 1),
       ('79181234592', 4, 400, 1),
       ('79181234596', 4, 400, 1),
       ('79181234597', 2, 400, 1),
       ('79181234598', 2, 400, 1),
       ('79181234599', 2, 400, 1),
       ('79181234600', 2, 400, 1),
       ('79181234601', 2, 400, 1),
       ('79181234602', 2, 400, 1),
       ('79181234603', 2, 400, 1),
       ('79181234604', 2, 400, 1),
       ('79181234605', 2, 400, 1),
       ('79181234606', 2, 400, 1),
       ('79181234607', 2, 400, 1);

INSERT INTO subscriber(phone_number, tariff_id, balance, operator_id)
VALUES ('79181111110', 1, -400, 1),
       ('79181111111', 2, -400, 1),
       ('79181111112', 3, -400, 1),
       ('79181111113', 4, -400, 1),
       ('79181111114', 2, -400, 1);

INSERT INTO subscriber(phone_number, tariff_id, balance, operator_id)
VALUES ('79180000000', 4, 400, 1);

INSERT INTO subscriber(phone_number, tariff_id, balance, operator_id)
VALUES ('79182222220', 1, 400, 2),
       ('79182222221', 2, 400, 2),
       ('79182222222', 3, 400, 2),
       ('79182222223', 4, 400, 2),
       ('79182222224', 2, 400, 2);

INSERT INTO subscriber(phone_number, tariff_id, balance, operator_id)
VALUES ('79210017003', 1, 0, 1);

CREATE TABLE IF NOT EXISTS billing_report
(
    id           SERIAL PRIMARY KEY,
    phone_number VARCHAR REFERENCES subscriber (phone_number) NOT NULL,
    call_type    VARCHAR(2)                                   NOT NULL,
    call_start   TIMESTAMP                                    NOT NULL,
    call_end     TIMESTAMP                                    NOT NULL,
    duration     TIME                                         NOT NULL,
    cost         NUMERIC(9, 2)                                NOT NULL,
    CONSTRAINT unique_report UNIQUE (phone_number, call_type, call_start, call_end, cost)
);

----------------------------------------------
CREATE TABLE IF NOT EXISTS cdr_info
(
    id           SERIAL PRIMARY KEY,
    call_type    VARCHAR,
    phone_number VARCHAR,
    call_start   VARCHAR,
    call_end     VARCHAR
);

-- INSERT INTO cdr_info (call_type, phone_number, call_start, call_end)
-- VALUES ('01', '79181234567', '20230403105951', '20230403164711'),
--        ('02', '79181234567', '20230403190125', '20230403191905'),
--        ('01', '79181234567', '20230403230237', '20230404000518'),
--        ('02', '79181234568', '20230404105951', '20230404155951'),
--        ('01', '79181234568', '20230405173623', '20230405173901'),
--        ('01', '79181234568', '20230405191931', '20230405193645'),
--        ('02', '79181234569', '20230406112233', '20230406113344'),
--        ('01', '79181234569', '20230406135500', '20230406135707'),
--        ('02', '79181234569', '20230406210115', '20230406210117'),
--        ('02', '79181234570', '20230407145415', '20230407150425'),
--        ('02', '79181234570', '20230407191427', '20230407231427'),
--        ('02', '79181234570', '20230408080101', '20230408123415'),
--        ('01', '79181234571', '20230409090909', '20230409090909'),
--        ('02', '79181234572', '20230410100000', '20230410100100'),
--        ('01', '79181234573', '20230410192516', '20230410192517'),
--        ('01', '79181111110', '20230410192516', '20230410192517'),
--        ('01', '79182222220', '20230410192516', '20230410192517');
--
-- INSERT INTO cdr_info (call_type, phone_number, call_start, call_end)
-- VALUES ('01', '79181234574', '20230411111111', '20230411111111'),
--        ('02', '79181234575', '20230412233341', '20230412233441'),
--        ('01', '79181234576', '20230413171851', '20230413174749'),
--        ('02', '79181234576', '20230413214017', '20230413224537'),
--        ('01', '79181234576', '20230413230530', '20230413230532'),
--        ('02', '79181234577', '20230414000000', '20230414000001'),
--        ('02', '79181111111', '20230414000000', '20230414000001'),
--        ('02', '79182222221', '20230414000000', '20230414000001');
--
-- INSERT INTO cdr_info (call_type, phone_number, call_start, call_end)
-- VALUES ('02', '79181234578', '20230415151515', '20230415151515'),
--        ('02', '79181234579', '20230416161616', '20230416161716'),
--        ('02', '79181234580', '20230417012141', '20230417110147'),
--        ('02', '79181234580', '20230418210121', '20230419235959'),
--        ('01', '79181234581', '20230420081618', '20230420105631'),
--        ('01', '79181234581', '20230421152134', '20230421153144'),
--        ('01', '79181234581', '20230421190021', '20230421191238'),
--        ('01', '79181234582', '20230422041715', '20230422090021'),
--        ('02', '79181234582', '20230422121212', '20230422125212'),
--        ('02', '79181234582', '20230422223112', '20230422223609'),
--        ('01', '79181234583', '20230423160000', '20230423174000'),
--        ('01', '79181234583', '20230423180120', '20230423181120'),
--        ('02', '79181234583', '20230423230001', '20230423235901'),
--        ('01', '79181234584', '20230424112541', '20230424120541'),
--        ('02', '79181234584', '20230424181814', '20230424183854'),
--        ('01', '79181234585', '20230425074140', '20230425081140'),
--        ('01', '79181234585', '20230425141500', '20230425141859'),
--        ('01', '79181234585', '20230425210000', '20230425210110'),
--        ('01', '79181234586', '20230426035959', '20230426045959'),
--        ('01', '79181234586', '20230426051515', '20230426062535'),
--        ('01', '79181234586', '20230426160521', '20230426165531'),
--        ('01', '79181234587', '20230427010101', '20230427010101'),
--        ('01', '79181234588', '20230428040404', '20230428040504'),
--        ('01', '79181234589', '20230429000005', '20230429000006'),
--        ('01', '79181111112', '20230429000005', '20230429000006'),
--        ('01', '79182222222', '20230429000005', '20230429000006');
--
-- INSERT INTO cdr_info (call_type, phone_number, call_start, call_end)
-- VALUES ('01', '79181234590', '20230401151400', '20230401151400'),
--        ('02', '79181234591', '20230402032050', '20230402032150'),
--        ('01', '79181234592', '20230403151800', '20230403155858'),
--        ('02', '79181234592', '20230403211415', '20230403211909'),
--        ('01', '79180000000', '20230404125959', '20230404125959'),
--        ('02', '79180000000', '20230405150107', '20230405150207'),
--        ('01', '79180000000', '20230406211201', '20230406213651'),
--        ('02', '79180000000', '20230406221221', '20230406221849'),
--        ('01', '79181234596', '20230407130000', '20230407131547'),
--        ('02', '79181234596', '20230407165115', '20230407170115'),
--        ('01', '79180000000', '20230407191910', '20230407193130'),
--        ('02', '79180000000', '20230407220550', '20230407220940'),
--        ('01', '79181111113', '20230407220550', '20230407220940'),
--        ('01', '79182222223', '20230407220550', '20230407220940');


-- обработка некорректных данных. В генераторе больше не будет таких краевых случаев
INSERT INTO cdr_info (call_type, phone_number, call_start, call_end)
VALUES ('02', '79181234597', '20230408161800', '20230408160800'),
       ('02', '79181234598', '20230409150000', '20230410165959'),
       ('03', '79181234599', '20230410141715', '20230410142700'),
       ('01', '7918123459', '20230410141715', '20230410142700'),
       ('01', '7918123459 п', '20230410141715', '20230410142700'),
       ('01', '79181234600', '2023041115002', '20230411151226'),
       ('01', '79181234601', '2023041215002 п', '20230412151226'),
       ('01', '79181234602', '13042023150000', '13042023155051'),
       ('02', '79181234603', '20230414154100', '20230414154720, привет'),
       ('02', '79181234603', '20230414154100', ''),
       ('01', '79181234604', '202304159900', '202304160100'),
       ('02', '79181234605', '20230416120000', '20230416121000'),
       ('02', '79181234606', '20230416120000', '20230416121000'),
       ('02', '79181234607', '20230416120000', '20230416121000'),
       ('02', '79181111114', '20230416120000', '20230416121000'),
       ('02', '79182222224', '20230416120000', '20230416121000');

-- INSERT INTO cdr_info (call_type, phone_number, call_start, call_end)
-- VALUES ('02', '79210017003', '20230408000000', '20230408010000');









