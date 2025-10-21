# Lambda Functions
resource "aws_lambda_function" "get_products" {
  filename         = data.archive_file.lambda_zip.output_path
  function_name    = "${var.app_name}-get-products"
  role            = aws_iam_role.lambda_execution_role.arn
  handler         = "org.springframework.cloud.function.adapter.aws.SpringBootStreamHandler"
  runtime         = "provided.al2023"
  architectures   = ["arm64"]
  timeout         = 30
  memory_size     = 1024
  source_code_hash = data.archive_file.lambda_zip.output_base64sha256

  environment {
    variables = {
      SPRING_CLOUD_FUNCTION_DEFINITION      = "getAllProductsFunction"
      PRODUCT_TABLE_NAME   = aws_dynamodb_table.products_table.name
    }
  }

  tracing_config {
    mode = "Active"
  }

  tags = {
    Name        = "${var.app_name}-get-products"
    Environment = var.environment
  }
}

resource "aws_lambda_function" "get_product_by_id" {
  filename         = data.archive_file.lambda_zip.output_path
  function_name    = "${var.app_name}-get-product-by-id"
  role            = aws_iam_role.lambda_execution_role.arn
  handler         = "org.springframework.cloud.function.adapter.aws.SpringBootStreamHandler"
  runtime         = "provided.al2023"
  architectures   = ["arm64"]
  timeout         = 30
  memory_size     = 1024
  source_code_hash = data.archive_file.lambda_zip.output_base64sha256

  environment {
    variables = {
      SPRING_CLOUD_FUNCTION_DEFINITION      = "getProductByIdFunction"
      PRODUCT_TABLE_NAME   = aws_dynamodb_table.products_table.name
    }
  }

  tracing_config {
    mode = "Active"
  }

  tags = {
    Name        = "${var.app_name}-get-product-by-id"
    Environment = var.environment
  }
}

resource "aws_lambda_function" "put_product" {
  filename         = data.archive_file.lambda_zip.output_path
  function_name    = "${var.app_name}-put-product"
  role            = aws_iam_role.lambda_execution_role.arn
  handler         = "org.springframework.cloud.function.adapter.aws.SpringBootStreamHandler"
  runtime         = "provided.al2023"
  architectures   = ["arm64"]
  timeout         = 30
  memory_size     = 1024
  source_code_hash = data.archive_file.lambda_zip.output_base64sha256

  environment {
    variables = {
      SPRING_CLOUD_FUNCTION_DEFINITION      = "createProductFunction"
      PRODUCT_TABLE_NAME   = aws_dynamodb_table.products_table.name
    }
  }

  tracing_config {
    mode = "Active"
  }

  tags = {
    Name        = "${var.app_name}-put-product"
    Environment = var.environment
  }
}

resource "aws_lambda_function" "delete_product" {
  filename         = data.archive_file.lambda_zip.output_path
  function_name    = "${var.app_name}-delete-product"
  role            = aws_iam_role.lambda_execution_role.arn
  handler         = "org.springframework.cloud.function.adapter.aws.SpringBootStreamHandler"
  runtime         = "provided.al2023"
  architectures   = ["arm64"]
  timeout         = 30
  memory_size     = 1024
  source_code_hash = data.archive_file.lambda_zip.output_base64sha256

  environment {
    variables = {
      SPRING_CLOUD_FUNCTION_DEFINITION      = "deleteProductFunction"
      PRODUCT_TABLE_NAME   = aws_dynamodb_table.products_table.name
    }
  }

  tracing_config {
    mode = "Active"
  }

  tags = {
    Name        = "${var.app_name}-delete-product"
    Environment = var.environment
  }
}

# DynamoDB policies
resource "aws_iam_role_policy" "lambda_dynamodb_read_policy" {
  name = "${var.app_name}-lambda-dynamodb-read-policy"
  role = aws_iam_role.lambda_execution_role.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "dynamodb:GetItem",
          "dynamodb:Query",
          "dynamodb:Scan"
        ]
        Resource = aws_dynamodb_table.products_table.arn
      }
    ]
  })
}

resource "aws_iam_role_policy" "lambda_dynamodb_write_policy" {
  name = "${var.app_name}-lambda-dynamodb-write-policy"
  role = aws_iam_role.lambda_execution_role.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "dynamodb:GetItem",
          "dynamodb:PutItem",
          "dynamodb:UpdateItem",
          "dynamodb:DeleteItem",
          "dynamodb:Query",
          "dynamodb:Scan"
        ]
        Resource = aws_dynamodb_table.products_table.arn
      }
    ]
  })
}

resource "aws_lambda_function" "delete_account" {
  filename         = data.archive_file.lambda_zip.output_path
  function_name    = "${var.app_name}-delete-account"
  role            = aws_iam_role.lambda_execution_role.arn
  handler         = "org.springframework.cloud.function.adapter.aws.SpringBootStreamHandler"
  runtime         = "provided.al2023"
  architectures   = ["arm64"]
  timeout         = 30
  memory_size     = 1024
  source_code_hash = data.archive_file.lambda_zip.output_base64sha256

  environment {
    variables = {
      SPRING_CLOUD_FUNCTION_DEFINITION = "deleteAccountFunction"
      PRODUCT_TABLE_NAME               = aws_dynamodb_table.products_table.name
    }
  }

  tracing_config {
    mode = "Active"
  }

  tags = {
    Name        = "${var.app_name}-delete-account"
    Environment = var.environment
  }
}

