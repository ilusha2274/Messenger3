

CREATE TABLE authorities (
    authority text NOT NULL,
    user_id integer NOT NULL
);


ALTER TABLE public.authorities OWNER TO postgres;



CREATE TABLE public.chats (
    chat_id integer NOT NULL,
    chat_last_message integer,
    chat_type text NOT NULL,
    name_chat text
);


ALTER TABLE public.chats OWNER TO postgres;



ALTER TABLE public.chats ALTER COLUMN chat_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.chats_chat_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);




CREATE TABLE public.messages (
    message_id integer NOT NULL,
    text_message text NOT NULL,
    chat_id integer NOT NULL,
    user_id integer NOT NULL,
    date_message timestamp with time zone
);


ALTER TABLE public.messages OWNER TO postgres;



ALTER TABLE public.messages ALTER COLUMN message_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.messages_message_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);




CREATE TABLE public.users (
    user_id integer NOT NULL,
    user_name text NOT NULL,
    user_email character varying(30) NOT NULL,
    user_password character varying(60) NOT NULL,
    enabled boolean NOT NULL
);


ALTER TABLE public.users OWNER TO postgres;


CREATE TABLE public.users_chats (
    user_id integer NOT NULL,
    chat_id integer NOT NULL
);


ALTER TABLE public.users_chats OWNER TO postgres;



ALTER TABLE public.users ALTER COLUMN user_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.users_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);




CREATE TABLE public.users_users (
    user1_id integer NOT NULL,
    user2_id integer NOT NULL
);


ALTER TABLE public.users_users OWNER TO postgres;

ALTER TABLE ONLY public.authorities
    ADD CONSTRAINT authorities_pkey PRIMARY KEY (user_id);




ALTER TABLE ONLY public.chats
    ADD CONSTRAINT chats_pkey PRIMARY KEY (chat_id);




ALTER TABLE ONLY public.messages
    ADD CONSTRAINT messages_pkey PRIMARY KEY (message_id);


ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (user_id);


ALTER TABLE ONLY public.messages
    ADD CONSTRAINT chat_id_key FOREIGN KEY (chat_id) REFERENCES public.chats(chat_id) NOT VALID;


ALTER TABLE ONLY public.chats
    ADD CONSTRAINT chats_chat_last_message_fkey FOREIGN KEY (chat_last_message) REFERENCES public.messages(message_id) NOT VALID;


ALTER TABLE ONLY public.users_users
    ADD CONSTRAINT user1_id_fkey FOREIGN KEY (user1_id) REFERENCES public.users(user_id) NOT VALID;



ALTER TABLE ONLY public.users_users
    ADD CONSTRAINT user2_id_fkey FOREIGN KEY (user2_id) REFERENCES public.users(user_id) NOT VALID;


ALTER TABLE ONLY public.authorities
    ADD CONSTRAINT user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(user_id) NOT VALID;


ALTER TABLE ONLY public.messages
    ADD CONSTRAINT user_id_key FOREIGN KEY (user_id) REFERENCES public.users(user_id) NOT VALID;



ALTER TABLE ONLY public.users_chats
    ADD CONSTRAINT users_chats_chat_id_fkey FOREIGN KEY (chat_id) REFERENCES public.chats(chat_id) NOT VALID;


ALTER TABLE ONLY public.users_chats
    ADD CONSTRAINT users_chats_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(user_id) NOT VALID;

