package com.play4ubot.utilities;

public enum BotConstants {
    NOT_IN_VOICE_CHANNEL("Conecte-se a um canal de voz"),
    SELF_NOT_IN_VOICE_CHANNEL("Eu preciso estar conectado a um canal de voz"),
    NO_ARGUMENT_TO_MUSIC("Comando *PLAY* precisa do nome da música"),
    NO_ARGUMENT_TO_DELETE("Comando *DEL* precisa do nome da música"),
    NOT_IN_SAME_VOICE_CHANNEL("Você precisa estar no mesmo canal de voz que eu"),
    INVALID_FILE_FORMAT("Extensão de arquivo inválida"),
    EXCESS_OF_FILES("Envie apenas um arquivo"),
    INVALID_PREFIX("Pré-fixo inválido, use símbolos válidos"),
    NOT_FOUND_FILE("Envie o arquivo da música"),
    PLAY_SYNTAX("\"Pré-fixo\"Play {Nome da música/Anexo do arquivo/URL}"),
    PREFIX_SYNTAX("\"Pré-fixo\"Prefixo {Símbolo}"),
    ADD_SYNTAX("\"Pré-fixo\"Add {Anexo do arquivo}"),
    DEL_SYNTAX("\"Pré-fixo\"Del {Nome da música}"),
    BANK_SYNTAX("\"Pré-fixo\"Banco {(opcional) Nome}");
    private final String constants;
    BotConstants (String constants){
        this.constants = constants;
    }

    public String getConstants() {
        return constants;
    }
}

