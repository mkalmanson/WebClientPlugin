package webclient

import grails.converters.JSON

class WebClientService {

    def get(String url, Class type) {
        String jsonString = """
{
  url: "${url}",
  name: "Some name",
  child: {
    name: "Child name"
    }
  }
"""
        def jsonObj = JSON.parse(jsonString)
        type.newInstance(jsonObj)
    }
}
