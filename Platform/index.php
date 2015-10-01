<!Doctype html>
<html>
<head>
	<title>RIDashboard</title>

	<!-- Include JQuery -->
	<script src="https://code.jquery.com/jquery-2.1.4.min.js">M</script>

	<!-- Include Bootstrap -->
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
	<script src="main.js"></script>

	<link rel="stylesheet" href="main.css" type='text/css'>



</head>



<body>

	<div class="nav-side-menu">
		<div class="brand">RIDashboard</div>
		<i class="fa fa-bars fa-2x toggle-btn" data-toggle="collapse" data-target="#menu-content"></i>

		<div class="menu-list">

			<ul id="menu-content" class="menu-content collapse out">


				<li  data-toggle="collapse" data-target="#products" class="collapsed">
					<a href="#"><i class="fa fa-gift fa-lg"></i> Upload <span class="arrow"></span></a>
				</li>
				<ul class="sub-menu collapse" id="products">
					<li><a href="#myModal" data-toggle="modal" data-target="#myModal">Aspect</a></li>
					<li><a href="#">Source</a></li>
				</ul>
				<li  data-toggle="collapse" data-target="#service" class="collapsed">
					<a href="#"><i class="fa fa-gift fa-lg"></i> View <span class="arrow"></span></a>
				</li>
				<ul class="sub-menu collapse" id="service">
					<li><a href="#">Aspects</a></li>
					<li><a href="#">Sources</a></li>
				</ul>


			</ul>
		</div>
	</div>

	<!-- Modal -->
	<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
					<h4 class="modal-title" id="myModalLabel">Upload Aspect</h4>
				</div>

				<form enctype="multipart/form-data" action="php/upload.php" method="post">

				<div class="modal-body">
					
						<div class="form-group">
							<input type="text" class="form-control" id="name" name="name" placeholder="Name" required>
						</div>

						<div class="form-group">
							<textarea class="form-control" type="textarea" id="message" name="description" placeholder="Description" maxlength="140" rows="7"></textarea>                   
						</div>
					</div>

					<div class="form-group">
							<input name="uploadedfile" type="file"></input>
					</div>
	
					<div class="modal-footer">
						<input type="submit" value="Upload" class="btn btn-primary btn-block"></input>
					</div>
				</div>
			</form>
			</div>
		</div>
	</body>
	</html>