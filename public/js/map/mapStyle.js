const BORDER_SIZE = 4;
const panel = document.getElementById("control");


$("#resizeLeft").on("click", function() {
    resize('left');
});

$("#resizeRight").on("click", function() {
    resize('right');
});



function resize(direction) {

    let $colLeft = $("#map");
    let $colRight = $("#control");

    let colLeftSizeClass = $colLeft.attr('class');
    let colRightSizeClass = $colRight.attr('class');

    let colLeftSize = parseInt(colLeftSizeClass.substr(colLeftSizeClass.length -1, colLeftSizeClass.length));
    let colRightSize = parseInt(colRightSizeClass.substr(colRightSizeClass.length -1, colRightSizeClass.length));

    if (direction === 'left') {
        if (colLeftSize > 5) {
            colLeftSize += -1;
            colRightSize += 1;
        }
    } else if (direction === 'right') {
        if (colRightSize > 3) {
            colLeftSize += 1;
            colRightSize += -1;
        }
    }

    let colLeftNewSizeClass = colLeftSizeClass.substr(0, colLeftSizeClass.length -1) + colLeftSize.toString();
    let colRightNewSizeClass = colRightSizeClass.substr(0, colRightSizeClass.length -1) + colRightSize.toString();


    $colLeft.toggleClass(colLeftSizeClass + " " + colLeftNewSizeClass);
    $colRight.toggleClass(colRightSizeClass + " " + colRightNewSizeClass);


}

// panel.addEventListener("mousedown", function(e){
//     console.log(e.offsetX);
//     if (e.offsetX < BORDER_SIZE) {
//         m_pos = e.x;
//         document.addEventListener("mousemove", resize, false);
//     }
// }, false);
//
// document.addEventListener("mouseup", function(){
//     document.removeEventListener("mousemove", resize, false);
// }, false);
