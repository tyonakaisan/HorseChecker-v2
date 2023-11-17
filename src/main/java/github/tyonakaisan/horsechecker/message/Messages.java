package github.tyonakaisan.horsechecker.message;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@SuppressWarnings("SpellCheckingInspection")
@DefaultQualifier(NonNull.class)
public enum Messages {
    //PREFIX
    PREFIX("<white>[<gradient:#ff2e4a:#ffd452>HorseChecker-v2</gradient>] </white>"),

    //COMMAND
    SHOW_STATS_ENABLED("<color:#59ffa4>ステータス表示中!</color>"),
    SHOW_STATS_DISABLED("<color:#ff4775>ステータス非表示中!</color>"),
    CANCEL_BREEDING_ENABLED("<color:#59ffa4>連続餌やり無効中!</color>"),
    CANCEL_BREEDING_DISABLED("<color:#ff4775>連続餌やり有効中!</color>"),
    CONFIG_RELOAD("<color:#59ffa4>configリロード完了!</color>"),
    COMMAND_INTERVAL("<color:#ff4775>このコマンドは<color:#ffc414><interval></color>秒後に使用できます!</color>"),

    //STATS
    STATS_RESULT_SCORE("Score: <rankcolor><rank></rankcolor><newline>"),
    STATS_RESULT_SPEED("Speed: <#ffa500><speed></#ffa500>blocks/s<newline>"),
    STATS_RESULT_JUMP("Jump : <#ffa500><jump></#ffa500>blocks<newline>"),
    STATS_RESULT_HP("MaxHP: <#ffa500><health></#ffa500><red>♥</red><newline>"),
    STATS_RESULT_OWNER("<owner>"),

    //BREEDING
    BREEDING_COOL_TIME("<color:#ff4775>繫殖クールタイム中(<color:#ffc414><cooltime>s</color>)は出来ません!</color>"),
    LOVE_MODE_TIME("<color:#ff4775>繫殖モード中(<color:#ffc414><cooltime>s</color>)は出来ません!</color>"),

    //SHARE
    TARGETED_ENTITY_IS_NULL("<color:#ff4775>mobを見て使用してください!</color>"),
    NOT_ALLOWED_SHARE("<color:#ff4775>馬の共有は許可されていません!</color>"),
    UNSHAREABLE_ENTITY("<color:#ff4775>そのmobは共有できません!</color>"),
    DIFFERENT_OWNER("<color:#ff4775>所有者が違うため共有できません!</color>"),
    BROADCAST_SHARE("<myhover><prefix><color:#5cb8ff><player></color><white>が<random_message><rankcolor><horse_name></rankcolor>を共有しました！</white><newline><b><gray>[カーソルを合わせて表示]</gray></b></myhover>"),
    BROADCAST_SHARE_SUCSESS("<color:#59ffa4>共有メッセージを送りました!"),

    //DEBUG
    SPAWN_HORSE("<color:#59ffa4>馬を召喚しました!</color> (sp:<color:#ffc414><speed></color>/jp:<color:#ffc414><jump></color>)"),
    ;

    private final String message;
    Messages(
            String message
    ) {
        this.message = message;
    }

    public String get() {
        return this.message;
    }

    public String getMessageWithPrefix() {
        return PREFIX.get() + this.message;
    }
}
