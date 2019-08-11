$("#resizeLeft").on("click", function() {
    resize('left');
});

$("#resizeRight").on("click", function() {
    resize('right');
});

/**
 * Changed widths of the map and control panel depending on input.
 * It does this by changing the classes of the map and control.
 * Min width of control is 3.
 * Min width of map is 5.
 * @param direction either value 'left' or 'right'
 */
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