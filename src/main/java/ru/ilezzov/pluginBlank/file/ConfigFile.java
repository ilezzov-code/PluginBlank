package ru.ilezzov.pluginBlank.file;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import eu.okaeri.configs.annotation.Header;
import ru.ilezzov.pluginBlank.permission.Permissions;

import java.util.concurrent.TimeUnit;


@Header({
        "██████╗░██╗░░░░░██╗░░░██╗░██████╗░██╗███╗░░██╗██████╗░██╗░░░░░░█████╗░███╗░░██╗██╗░░██╗",
        "██╔══██╗██║░░░░░██║░░░██║██╔════╝░██║████╗░██║██╔══██╗██║░░░░░██╔══██╗████╗░██║██║░██╔╝",
        "██████╔╝██║░░░░░██║░░░██║██║░░██╗░██║██╔██╗██║██████╦╝██║░░░░░███████║██╔██╗██║█████═╝░",
        "██╔═══╝░██║░░░░░██║░░░██║██║░░╚██╗██║██║╚████║██╔══██╗██║░░░░░██╔══██║██║╚████║██╔═██╗░",
        "██║░░░░░███████╗╚██████╔╝╚██████╔╝██║██║░╚███║██████╦╝███████╗██║░░██║██║░╚███║██║░╚██╗",
        "╚═╝░░░░░╚══════╝░╚═════╝░░╚═════╝░╚═╝╚═╝░░╚══╝╚═════╝░╚══════╝╚═╝░░╚═╝╚═╝░░╚══╝╚═╝░░╚═╝",
        "",
        "Developer / Разработчик: ILeZzoV",
        "More plugins / Больше плагинов: https://t.me/ilezzov_plugins",
        "",
        "Socials / Ссылки:",
        "• Contact with me / Связаться: https://t.me/ilezovofficial",
        "• Telegram Channel / Телеграм канал: https://t.me/ilezzov_plugins",
        "• GitHub: https://github.com/ilezzov-code",
        "",
        "By me coffee / Поддержать разработчика:",
        "• DA: https://www.donationalerts.com/r/ilezov",
        "• YooMoney: https://yoomoney.ru/to/4100118180919675",
        "• Telegram Gift: https://t.me/ilezovofficial",
        "• TON: UQBENFaqzv19GBR1EnDKad_nAOB--Ofsdk1FeRU7W3xprRLY",
        "• BTC: bc1qn0f9yvcsacvedjk6qzu9cvagchkcz28gqrg2eh",
        "• ETH: 0xbE7f260b952b87578079C0D6E03d3641425718c9",
        "• Card: 2200700733487101",
        ""
})
public class ConfigFile extends OkaeriConfig {

    @Comment({
            "Supporting messages languages / Доступные языки сообщений:",
            "en-US, ru-RU"
    })
    public String language = "en-US";

    @Comment("")
    @CustomKey("version-control")
    public VersionControl versionControl = new VersionControl();

    public static class VersionControl extends OkaeriConfig {

        @Comment({
                "Check for updates on server startup",
                "Проверять ли наличие обновлений при запуске сервера"
        })
        @CustomKey("check-on-startup")
        public boolean checkOnStartup = true;

        @Comment({
                "",
                "Background update check interval",
                "Интервал автоматической проверки обновлений в фоне"
        })
        @CustomKey("check-interval")
        public Interval checkInterval = new Interval(true, TimeUnit.HOURS, 6);

        @Comment({
                "",
                "Notify administrators about new versions on join",
                "Отправлять ли администраторам уведомление о наличии новой версии при входе на сервер",
                "",
                "Permission / Право: " + Permissions.VERSION_NOTIFY
        })
        @CustomKey("notify-admins-on-join")
        public boolean notifyAdminsOnJoin = true;


        @Comment("")
        public Security security = new Security();
    }

    public static class Interval extends OkaeriConfig {
        public boolean enable;
        @Comment("TimeUnit: SECONDS, MINUTES, HOURS, DAYS")
        public TimeUnit unit;
        public int value;

        public Interval() {}

        public Interval(boolean enable, TimeUnit unit, int value) {
            this.enable = enable;
            this.unit = unit;
            this.value = value;
        }
    }

    public static class Security extends OkaeriConfig {

        @Comment({
                "Automatically disable the plugin if the current version is no longer supported or if a critical vulnerability is found",
                "Warning: Setting true may disable important functionality",
                "",
                "Автоматическое отключение плагина, если текущая версия больше не поддерживается или если обнаружена критическая уязвимость",
                "Внимание: Установка значения true может привести к отключению важной функциональности"
        })
        @CustomKey("lockdown-on-critical")
        public boolean lockdownOnCritical = false;

        @Comment({
                "",
                "How often to spam online admins in chat",
                "about the need to urgently install a critical patch",
                "Set to 0 to notify only once on join",
                "",
                "Как часто спамить администраторам онлайн в чат",
                "о необходимости срочно установить критический патч",
                "Установите 0, чтобы уведомлять только один раз при входе",
                "",
                "Permission / Право" + Permissions.VERSION_NOTIFY
        })
        @CustomKey("critical-notify-interval")
        public Interval criticalNotifyInterval = new Interval(true, TimeUnit.MINUTES, 10);
    }

    @Comment("")
    public boolean debug = false;
}
