package cryptography

import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.awt.Color
import java.io.File

fun getBit(value: Int, position: Int): Int {
    return (value shr position) and 1;
}
fun encryptMessage(message:ByteArray,password:ByteArray):ByteArray{
    return ByteArray(message.size){
        (message[it].toInt() xor password[it % password.size].toInt()).toByte()
    }
}

fun hide() {
    println("Input image file:")
    val iFileName = readln()
    println("Output image file:")
    val oFileName = readln()
    println("Message to hide:")
    val message = readln()
    println("Password:")
    val pass = readln()
    val convertedPass = pass.encodeToByteArray()
//    val iFileName = "D://tmp/11/in.png"
//    val oFileName = "D://tmp/11/out.png"
//    val message = "My very secret message"
    val convertedMessage = encryptMessage( message.encodeToByteArray(),convertedPass) + byteArrayOf(0, 0, 3)
    //reading files
    try {
        val inputFile = File(iFileName)
        val image: BufferedImage = ImageIO.read(inputFile)
        if (convertedMessage.size * 8 > image.width * image.height) {
            throw Exception("The input image is not large enough to hold this message.")
        }
        image@ for (y in 0 until image.height) {
            for (x in 0 until image.width) {

                val color = Color(image.getRGB(x, y))
                val index = (y * image.width + x) / 8
                val bit = (y * image.width + x) % 8
                if (index > convertedMessage.lastIndex) {
                    break@image
                }
                var b = color.blue and 1.inv()
                b = b or getBit(convertedMessage[index].toInt(), 7 - bit)
                val colorNew = Color(color.red, color.green, b)
                image.setRGB(x, y, colorNew.rgb)
            }
        }
        val outputFile = File(oFileName)
        ImageIO.write(image, "png", outputFile)
        println("Message saved in $oFileName image.")
    } catch (ex: Exception) {
        println(ex.message)
    }
}

fun show() {
    println("Input image file:")
    val iFileName = readln()
    println("Password:")
    val pass = readln()
    val convertedPass = pass.encodeToByteArray()
    //val iFileName = "D://tmp/11/out.png"
    val message = mutableListOf<Byte>()
    try {
        val inputFile = File(iFileName)
        val image: BufferedImage = ImageIO.read(inputFile)
        image@ for (y in 0 until image.height) {
            for (x in 0 until image.width) {

                val color = Color(image.getRGB(x, y))
                val index = (y * image.width + x) / 8
                val bit = (y * image.width + x) % 8
                if (message.size >= 3) {
                    if (message[message.lastIndex] == 3.toByte()
                        && message[message.lastIndex - 1] == 0.toByte()
                        && message[message.lastIndex - 2] == 0.toByte()
                    ) {
                        message.removeLast()
                        message.removeLast()
                        message.removeLast()
                        break@image
                    }
                }
                val b = color.blue and 1
                if (bit == 0) {
                    message.add(b.toByte())
                } else {
                    message[index] = (message[index].toInt() or (b shl (7 - bit))).toByte()
                }
            }
        }

        val messageStr = encryptMessage(message.toByteArray(),convertedPass).toString(Charsets.UTF_8)
        println("Message:")
        println(messageStr)
    } catch (ex: Exception) {
        println(ex.message)
    }
}

fun wrong(input: String) {
    println("Wrong task: $input")
}

fun main() {
    while (true) {
        println("Task (hide, show, exit):")
        when (val input = readln()) {
            "hide" -> hide()
            "show" -> show()
            "exit" -> break
            else -> wrong(input)
        }
    }
    println("Bye!")
}

