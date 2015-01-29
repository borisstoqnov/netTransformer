package net.itransformers.idiscover.v2.core.listeners.groovy

/**
 * Created by vasko on 1/29/2015.
 */

output << "<graphml>\n"

output << input.name.text() << "\n"
output.write(input.name.text() + "\n") // the same as above

input.object.findAll {
        it.objectType.text() == 'Discovery Interface'
    }.object.findAll {
        it.objectType.text() == 'IPv4 Address'
    }.parameters.parameter.findAll {
        it.name.text() == 'IPv4Address'
    }.eachWithIndex { ip, i ->
        output << "IP_${i+1}: " << ip.value.text() << "\n"
    }

output << "</graphml>\n"