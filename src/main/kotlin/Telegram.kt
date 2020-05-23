import khttp.structures.files.FileLike
import java.io.File

class Telegram(private val token: String) {

    /**
     * sendMessage — Send file (except photo or video)
     * https://core.telegram.org/bots/api#sendmessage
     *
     * chatId: @username or @channelname
     * text: Text message
     * parseMode: Markdown, MarkdownV2, HTML — https://core.telegram.org/bots/api#formatting-options (Default: Markdown)
     * disableWebPagePreview: true - Don't show web preview if text contain URLs (Default: false)
     * disableNotification: true - Readers will not receive notification (Default: false)
     */
    fun sendMessage(
            chatId: String,
            text: String,
            parseMode: String = "Markdown",
            disableWebPagePreview: Boolean = false,
            disableNotification: Boolean = false
    ): Boolean {
        val url = "https://api.telegram.org/bot$token/sendMessage"
        return khttp.post(
                url,
                data = mapOf(
                        "chat_id" to chatId,
                        "disable_notification" to disableNotification,
                        "disable_web_page_preview" to disableWebPagePreview,
                        "parse_mode" to parseMode,
                        "text" to text
                )
        ).jsonObject.getBoolean("ok")
    }

    /**
     * sendDocument — Send file (except photo or video)
     * https://core.telegram.org/bots/api#senddocument
     *
     * chatId: @username or @channelname
     * file: File object
     * caption: Text message (Default: "" (Empty string))
     * parseMode: Markdown, MarkdownV2, HTML — https://core.telegram.org/bots/api#formatting-options (Default: Markdown)
     * disableNotification: true - Readers will not receive notification (Default: false)
     */
    fun sendDocument(chatId: String,
                     file: File,
                     caption: String = "",
                     parseMode: String = "Markdown",
                     disableNotification: Boolean = true) = sendFile(
            method = "document",
            chatId = chatId,
            file = file,
            caption = caption,
            parseMode = parseMode,
            disableNotification = disableNotification)

    /**
     * sendPhoto — Send photo from file
     * https://core.telegram.org/bots/api#sendphoto
     *
     * chatId: @username or @channelname
     * file: Photo file
     * caption: Text message (Default: "" (Empty string))
     * parseMode: Markdown, MarkdownV2, HTML — https://core.telegram.org/bots/api#formatting-options (Default: Markdown)
     * disableNotification: true - Readers will not receive notification (Default: false)
     */
    fun sendPhoto(chatId: String,
                  file: File,
                  caption: String = "",
                  parseMode: String = "Markdown",
                  disableNotification: Boolean = true) = sendFile(
            method = "photo",
            chatId = chatId,
            file = file,
            caption = caption,
            parseMode = parseMode,
            disableNotification = disableNotification)

    /**
     * sendVideo — Send video from file
     * https://core.telegram.org/bots/api#sendvideo
     *
     * chatId: @username or @channelname
     * file: Video file
     * caption: Text message (Default: "" (Empty string))
     * parseMode: Markdown, MarkdownV2, HTML — https://core.telegram.org/bots/api#formatting-options (Default: Markdown)
     * disableNotification: true - Readers will not receive notification (Default: false)
     * videoWidth: Width. Optional.
     * videoHeight: Height. Optional.
     * videoDuration: Duration. Optional.
     * videoSupportsStreaming: true — video supports streaming. Optional.
     */
    fun sendVideo(chatId: String,
                  file: File,
                  caption: String = "",
                  parseMode: String = "Markdown",
                  disableNotification: Boolean = true,
                  videoWidth: Int? = null,
                  videoHeight: Int? = null,
                  videoDuration: Int? = null,
                  videoThumb: File? = null,
                  videoSupportsStreaming: Boolean? = false) = sendFile(
            method = "photo",
            chatId = chatId,
            file = file,
            caption = caption,
            parseMode = parseMode,
            disableNotification = disableNotification,
            videoWidth = videoWidth,
            videoHeight = videoHeight,
            videoDuration = videoDuration,
            videoThumb = videoThumb,
            videoSupportsStreaming = videoSupportsStreaming)

    /**
     * sendFile — base method for sending local files, photo and video
     *
     * method:  document, photo, video
     * chatId: @username or @channelname
     * caption: Text message
     * parseMode: Markdown, MarkdownV2, HTML — https://core.telegram.org/bots/api#formatting-options
     * disableNotification: true - Readers will not receive notification
     * videoWidth: Width. Optional. Only for videos.
     * videoHeight: Height. Optional. Only for videos.
     * videoDuration: Duration. Optional. Only for videos.
     * videoSupportsStreaming: true — video supports streaming. Optional. Only for videos.
     */
    private fun sendFile(
            method: String,
            chatId: String,
            file: File,
            caption: String = "",
            parseMode: String = "Markdown",
            disableNotification: Boolean = false,
            videoWidth: Int? = null,
            videoHeight: Int? = null,
            videoDuration: Int? = null,
            videoThumb: File? = null,
            videoSupportsStreaming: Boolean? = false

    ): Boolean {
        val files = mutableListOf(FileLike(method, file.name, file.readBytes()))

        val data: MutableMap<String, Any> = mutableMapOf(
                "chat_id" to chatId,
                "disable_notification" to disableNotification,
                "parse_mode" to parseMode,
                "caption" to caption
        )
        lateinit var apiMethod: String
        when (method) {
            "document" -> apiMethod = "sendDocument"
            "photo" -> apiMethod = "sendPhoto"
            "video" -> {
                apiMethod = "sendVideo"
                videoWidth?.let { data["width"] = it }
                videoHeight?.let { data["height"] = it }
                videoDuration?.let { data["duration"] = it }
                videoThumb?.let { files.add(FileLike("thumb", file.readBytes())) }
                videoSupportsStreaming?.let { data["supports_streaming"] = it }
            }
            else -> apiMethod = "unknown"
        }

        if (apiMethod == "unknown") {
            return false
        }

        val url = "https://api.telegram.org/bot$token/$apiMethod"

        return khttp.post(
                url,
                data = data,
                files = files
        ).jsonObject.getBoolean("ok")
    }
}