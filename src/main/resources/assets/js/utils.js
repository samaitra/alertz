/**
 * Created by IntelliJ IDEA.
 * User: deepthi.kulkarni
 * Date: 26/07/13
 * Time: 5:09 PM
 * To change this template use File | Settings | File Templates.
 */

String.prototype.isBlank = function() {
    return (this==null || this=="");
}

function getParam(name){
   return (name=(new RegExp('[?&]'+encodeURIComponent(name)+'=([^&]*)')).exec(location.search)) ?
      decodeURIComponent(name[1]) :
           "";
}

function defineErrorType(error, message) {
    $('#message').removeClass().addClass("alert alert-error");
    if(error.status == 404){
        $('#message').text("No results found matching the search criteria!");
    }
    if(error.status == 422){
        $('#message').text("One or more fields are empty! Please ensure that all fields marked mandatory are filled.");
    }
    else {
        $("#message").text(message);
    }
}