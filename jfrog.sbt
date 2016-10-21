
credentials ++= {
  for {
    username <- Option(System.getenv().get("JFROG_USERNAME"))
    apikey <- Option(System.getenv().get("JFROG_APIKEY"))
  } yield
    Credentials("Artifactory Realm", "oss.jfrog.org", username, apikey)
}.toSeq
