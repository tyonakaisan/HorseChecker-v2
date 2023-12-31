package github.tyonakaisan.horsechecker.message;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@SuppressWarnings("SpellCheckingInspection")
@DefaultQualifier(NonNull.class)
public enum Messages {

    //PREFIX
    PREFIX("<white>[<gradient:#ff2e4a:#ffd452>HorseChecker-v2</gradient>] </white>"),
    SHORT_PREFIX("<white>[<gradient:#ff2e4a:#ffd452>HCv2</gradient>] </white>"),

    //COMMAND
    SHOW_STATS_ENABLED("<color:#59ffa4>ステータス表示中!</color>"),
    SHOW_STATS_DISABLED("<color:#ff4775>ステータス非表示中!</color>"),
    CANCEL_BREEDING_ENABLED("<color:#ff4775>連続餌やり無効中!</color>"),
    CANCEL_BREEDING_DISABLED("<color:#59ffa4>連続餌やり有効中!</color>"),
    BREED_NOTIFICATION_ENABLED("<color:#59ffa4>ブリーディング通知有効中!</color>"),
    BREED_NOTIFICATION_DISABLED("<color:#ff4775>ブリーディング通知無効中!</color>"),
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
    BREEDING_NOTIFICATION("<myhover><call><prefix><color:#59ffa4>産まれた馬のステータスはこちら!</color><newline><b><gray>[クリックして場所を表示]</gray></call></myhover>"),
    BABY_HORSE_LOCATION("world: <gold><world></gold> <newline>x: <gold><x></gold> y: <gold><y></gold> z: <gold><z></gold>"),
    BABY_HORSE_NOT_FOUND("<color:#ff4775>その子は別の世界にいるか、もしくは存在してないようです…</color>"),

    //SHARE
    TARGETED_ENTITY_IS_NULL("<color:#ff4775>mobを見て使用してください!</color>"),
    NOT_ALLOWED_SHARE("<color:#ff4775>馬の共有は許可されていません!</color>"),
    UNSHAREABLE_ENTITY("<color:#ff4775>そのmobは共有できません!</color>"),
    DIFFERENT_OWNER("<color:#ff4775>所有者が違うため共有できません!</color>"),
    BROADCAST_SHARE("<myhover><prefix><color:#5cb8ff><player></color><white>が<random_message><rankcolor><horse_name></rankcolor>を共有しました!</white><newline><b><gray>[カーソルを合わせて表示]</gray></b></myhover>"),
    BROADCAST_SHARE_SUCCESS("<color:#59ffa4>共有メッセージを送りました!"),

    //DEBUG
    MAX_SPAWN("<color:#ff4775>1回のコマンドで出せる最大数は<max>体までです!</color>"),
    SPAWN_HORSE("<color:#59ffa4>馬を召喚しました!</color> (sp:<color:#ffc414><speed></color>/jp:<color:#ffc414><jump></color>)"),
    REMOVE_HORSE("<color:#ffc414><counts> </color><color:#59ffa4>体の馬が消されました...</color>")
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

    public String getMessageWithShortPrefix() {
        return SHORT_PREFIX.get() + this.message;
    }
}
