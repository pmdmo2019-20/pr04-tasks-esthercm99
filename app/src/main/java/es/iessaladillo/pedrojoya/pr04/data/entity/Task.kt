package es.iessaladillo.pedrojoya.pr04.data.entity

//  Crea una clase llamada Task con las siguientes propiedades:
//  id (Long), concept(String), createdAt (String),
//  completed (Boolean), completedAt (String)

class Task(var id: Long, val concept: String, val createdAt: String) {

    var completed: Boolean = false
    var completedAt: String = ""

}




