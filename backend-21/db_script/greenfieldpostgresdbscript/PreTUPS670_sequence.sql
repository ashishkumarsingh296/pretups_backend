
--
-- Name: seq_in_trans_id; Type: SEQUENCE; Schema: pretupsschema1; Owner: pgdb
--

CREATE SEQUENCE seq_in_trans_id
    START WITH 0
    INCREMENT BY 1
    MINVALUE 0
    MAXVALUE 999999999999999999
    CACHE 20;


ALTER TABLE seq_in_trans_id OWNER TO pgdb;

--
-- Name: seq_queue_id; Type: SEQUENCE; Schema: pretupsschema1; Owner: pgdb
--

CREATE SEQUENCE seq_queue_id
    START WITH 0
    INCREMENT BY 1
    MINVALUE 0
    MAXVALUE 99999999999999999
    CACHE 1;


ALTER TABLE seq_queue_id OWNER TO pgdb;

--
-- Name: transsumm_id; Type: SEQUENCE; Schema: pretupsschema1; Owner: pgdb
--

CREATE SEQUENCE transsumm_id
    START WITH 0
    INCREMENT BY 1
    MINVALUE 0
    MAXVALUE 999999999999
    CACHE 1;


ALTER TABLE transsumm_id OWNER TO pgdb;

--
-- Name: voucher_audit_id; Type: SEQUENCE; Schema: pretupsschema1; Owner: pgdb
--

CREATE SEQUENCE voucher_audit_id
    START WITH 0
    INCREMENT BY 1
    MINVALUE 0
    MAXVALUE 999999999999999999
    CACHE 1;


ALTER TABLE voucher_audit_id OWNER TO pgdb;
--
-- Name: iat_dwh_id; Type: SEQUENCE; Schema: pretupsschema1; Owner: pgdb
--

CREATE SEQUENCE iat_dwh_id
    START WITH 0
    INCREMENT BY 1
    MINVALUE 0
    MAXVALUE 999999999999
    CACHE 1;


ALTER TABLE iat_dwh_id OWNER TO pgdb;
