-- DB for Documents 
-- fit for Postgres 13.6 ver 

/* [ Documents Base ] */

-- Drop table

-- DROP TABLE public.document_base;

CREATE TABLE public.document_base (
	seq numeric(10) NOT NULL DEFAULT 0, -- 시퀀스
	up_seq numeric(10) NULL DEFAULT NULL::numeric, -- 상위시퀀스
	title varchar(500) NULL DEFAULT NULL::character varying, -- 제목
	contents text NULL, -- 내용
	reg_dttm timestamp NULL, -- 등록일
	reg_id varchar(100) NULL DEFAULT NULL::character varying, -- 등록아이디
	mod_dttm timestamp NULL, -- 수정일
	mod_id varchar(100) NULL DEFAULT NULL::character varying, -- 수정아이디
	file_master_seq numeric(10) NULL DEFAULT 0 -- 파일 마스터 시퀀스
);
COMMENT ON TABLE public.document_base IS '문서 파일';

-- Column comments

COMMENT ON COLUMN public.document_base.seq IS '시퀀스';
COMMENT ON COLUMN public.document_base.up_seq IS '상위시퀀스';
COMMENT ON COLUMN public.document_base.title IS '제목';
COMMENT ON COLUMN public.document_base.contents IS '내용';
COMMENT ON COLUMN public.document_base.reg_dttm IS '등록일';
COMMENT ON COLUMN public.document_base.reg_id IS '등록아이디';
COMMENT ON COLUMN public.document_base.mod_dttm IS '수정일';
COMMENT ON COLUMN public.document_base.mod_id IS '수정아이디';
COMMENT ON COLUMN public.document_base.file_master_seq IS '파일 마스터 시퀀스';

-- Permissions

ALTER TABLE public.document_base OWNER TO api;
GRANT ALL ON TABLE public.document_base TO api;


/* [ Documents File Master ] */

-- Drop table

-- DROP TABLE public.document_file_master;

CREATE TABLE public.document_file_master (
	master_seq numeric(10) NULL, -- 파일 마스터 시퀀스
	reg_id varchar(30) NULL, -- 등록자
	reg_dttm timestamp NULL -- 등록일
);
COMMENT ON TABLE public.document_file_master IS '파일 마스터 테이블';

-- Column comments

COMMENT ON COLUMN public.document_file_master.master_seq IS '파일 마스터 시퀀스';
COMMENT ON COLUMN public.document_file_master.reg_id IS '등록자';
COMMENT ON COLUMN public.document_file_master.reg_dttm IS '등록일';

-- Permissions

ALTER TABLE public.document_file_master OWNER TO api;
GRANT ALL ON TABLE public.document_file_master TO api;



/* [ Docuemts File ] */

-- Drop table

-- DROP TABLE public.document_file;

CREATE TABLE public.document_file (
	file_master_seq numeric(10) NULL, -- 파일 마스터 시퀀스
	file_seq numeric(10) NULL, -- 파일 시퀀스
	file_name varchar(50) NULL, -- 파일 이름
	file_path varchar NULL, -- 파일 경로
	file_type varchar(50) NULL, -- 파일 타입
	file_size varchar(50) NULL, -- 파일 사이즈
	file_ext varchar(10) NULL, -- 파일 확장자
	reg_dttm timestamp NULL, -- 등록일
	reg_id varchar(30) NULL, -- 등록자
	document_seq numeric(10) NULL -- 문서 연계 시퀀스 번호
);
COMMENT ON TABLE public.document_file IS '파일 업로드';

-- Column comments

COMMENT ON COLUMN public.document_file.file_master_seq IS '파일 마스터 시퀀스';
COMMENT ON COLUMN public.document_file.file_seq IS '파일 시퀀스';
COMMENT ON COLUMN public.document_file.file_name IS '파일 이름';
COMMENT ON COLUMN public.document_file.file_path IS '파일 경로';
COMMENT ON COLUMN public.document_file.file_type IS '파일 타입';
COMMENT ON COLUMN public.document_file.file_size IS '파일 사이즈';
COMMENT ON COLUMN public.document_file.file_ext IS '파일 확장자';
COMMENT ON COLUMN public.document_file.reg_dttm IS '등록일';
COMMENT ON COLUMN public.document_file.reg_id IS '등록자';
COMMENT ON COLUMN public.document_file.document_seq IS '문서 연계 시퀀스 번호';

-- Permissions

ALTER TABLE public.document_file OWNER TO api;
GRANT ALL ON TABLE public.document_file TO api;
