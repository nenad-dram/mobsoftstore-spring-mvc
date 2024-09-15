function newAppValidate() {
    let name = document.getElementById("name").value;
    let category = document.getElementById("category").value;
    let archive = document.getElementById("archive").value;

    document.getElementById("submit").disabled = (name == "" || category == "" || archive == "");
}